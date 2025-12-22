function cToF(c) {
  return c * 9 / 5 + 32;
}

function fToC(f) {
  return (f - 32) * 5 / 9;
}

function getScatterRows() {
  const rows = window.SUMMARY_DATA || [];
  const f = window.SCATTER_FILTER || "all";

  if (f === "all") return rows;

    return rows.filter(d => {
    const tempF = cToF(d.temperature_max);
    switch (f) {
      case "below32": return tempF < 32;
      case "32to50":  return tempF >= 32 && tempF < 50;
      case "50to70":  return tempF >= 50 && tempF < 70;
      case "70to90":  return tempF >= 70 && tempF < 90;
      case "above90": return tempF >= 90;
      default:        return true; // "all"
    }
  });
}

function initChart() {
  if (!Array.isArray(window.SUMMARY_DATA) || window.SUMMARY_DATA.length === 0) {
    console.error("SUMMARY_DATA missing");
    return;
  }
  if (!window.ANALYTICS_DATA) {
    console.error("ANALYTICS_DATA missing");
    return;
  }

  if (window.currentChart) {
    window.currentChart.destroy();
    window.currentChart = null;
  }

  const ctx = document.getElementById("chart").getContext("2d");

  const rows = getScatterRows();
  if (rows.length === 0) {
    console.warn("No rows for current filter:", window.SCATTER_FILTER);
    return;
  }

  const points = rows.map(d => ({
    x: cToF(d.temperature_max),
    y: d.incident_count,
    date: d.date
  }));

  const { alpha, beta } = window.ANALYTICS_DATA;

  // Line endpoints should match the currently displayed x-range
  const tempsF = points.map(p => p.x);
  const minF = Math.min(...tempsF);
  const maxF = Math.max(...tempsF);

  const regressionLine = [
    { x: minF, y: alpha + beta * fToC(minF) },
    { x: maxF, y: alpha + beta * fToC(maxF) }
  ];

  window.currentChart = new Chart(ctx, {
    type: "scatter",
    data: {
      datasets: [
    {
      label: "Daily Incidents",
      data: points,
      backgroundColor: "rgba(70, 130, 180, 0.20)",
      pointRadius: 4,
      pointHoverRadius: 6,
      order: 1
    },
    {
      label: "Regression Line (Global Model)",
      data: regressionLine,
      showLine: true,        // 🔑 THIS IS THE FIX
      borderColor: "rgba(229, 126, 67, 1)",
      borderWidth: 3,
      pointRadius: 0,
      fill: false,
      order: 0
    }
]

    },
    options: {
      responsive: true,
      maintainAspectRatio: false,

      plugins: {
        title: {
          display: true,
          text: "Daily Shooting Incidents vs. Maximum Temperature",
          font: { size: 16 }
        },
        tooltip: {
          callbacks: {
            label: function (context) {
              const p = context.raw;
              // Regression-line points won’t have date; guard it
              if (!p.date) return `High Temperature: ${p.x.toFixed(1)}°F, Predicted Incidents: ${p.y.toFixed(2)}`;
              return `${p.date}, High Temperature: ${p.x.toFixed(1)}°F, Total Incidents: ${p.y}`;
            }
          }
        },
        legend: {
          labels: { font: { size: 12 } }
        }
      },

      scales: {
        x: {
          ticks: { font: { size: 12 } },
          title: {
            display: true,
            text: "Maximum Daily Temperature (°F)",
            font: { size: 14 }
          }
        },
        y: {
          ticks: { font: { size: 12 } },
          title: {
            display: true,
            text: "Incident Count",
            font: { size: 14 }
          }
        }
      }
    }
  });
}
