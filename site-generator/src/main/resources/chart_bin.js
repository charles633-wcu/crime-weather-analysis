function cToF(c) {
  return c * 9 / 5 + 32;
}

/**
 * Smoothed rolling average over temperature.
 * windowSize = ± degrees Fahrenheit
 * step = temperature increment
 */
function computeSmoothedCurve(rows, windowSize = 1, step = 3) {
  const data = rows.map(d => ({
    temp: cToF(d.temperature_max),
    incidents: d.incident_count
  }));

  const temps = data.map(d => d.temp);
  const minT = Math.floor(Math.min(...temps));
  const maxT = Math.ceil(Math.max(...temps));

  const result = [];

  for (let T = minT; T <= maxT; T += step) {
    const window = data.filter(d => Math.abs(d.temp - T) <= windowSize);

    // Stability guard: avoid sparse extremes
    if (window.length < 10) continue;

    const avg =
      window.reduce((sum, d) => sum + d.incidents, 0) / window.length;

    result.push({ x: T, y: avg });
  }

  return result;
}

/**
 * Transparent binned averages for context.
 */
function computeTemperatureBins(rows, binSize = 10) {
  const data = rows.map(d => ({
    temp: cToF(d.temperature_max),
    incidents: d.incident_count
  }));

  const temps = data.map(d => d.temp);
  const minT = Math.floor(Math.min(...temps));
  const maxT = Math.ceil(Math.max(...temps));

  const bins = [];

  for (let t = minT; t <= maxT; t += binSize) {
    const bucket = data.filter(d => d.temp >= t && d.temp < t + binSize);

    if (bucket.length < 5) continue;

    const avg =
      bucket.reduce((s, d) => s + d.incidents, 0) / bucket.length;

    bins.push({
      x: t + binSize / 2, // center of bin
      y: avg
    });
  }

  return bins;
}

function initBinsChart() {
  if (!Array.isArray(window.SUMMARY_DATA) || window.SUMMARY_DATA.length === 0) {
    console.error("SUMMARY_DATA missing");
    return;
  }

  if (window.currentChart) {
    window.currentChart.destroy();
    window.currentChart = null;
  }

  const ctx = document.getElementById("chart").getContext("2d");

  const curve = computeSmoothedCurve(window.SUMMARY_DATA, 1, 3);
  const bins = computeTemperatureBins(window.SUMMARY_DATA, 10);

  window.currentChart = new Chart(ctx, {
    data: {
      datasets: [
        {
          type: "bar",
          label: "Binned Average (10°F)",
          data: bins,
          backgroundColor: "rgba(70, 130, 180, 0.45)",
          borderWidth: 0,
          order: 1
        },
        {
          type: "line",
          label: "Smoothed Trend",
          data: curve,
          borderColor: "rgba(229, 126, 67, 1)",
          borderWidth: 3,
          pointRadius: 0,
          tension: 0.3,
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
          text: "Average Daily Shooting Incidents by Temperature (Trend)",
          font: { size: 16 }
        },
        legend: {
          labels: { font: { size: 12 } }
        },
        tooltip: {
          callbacks: {
            label: function (context) {
              const x = context.parsed.x.toFixed(0);
              const y = context.parsed.y.toFixed(2);

              if (context.dataset.type === "bar") {
                return `Temperature: ${x}°F (bin)\nAverage: ${y} incidents`;
              }

              return `Temperature: ${x}°F\nComputed Average: ${y} incidents`;
            }
          }
        }
      },

      scales: {
        x: {
          type: "linear",
          title: {
            display: true,
            text: "Maximum Daily Temperature (°F)",
            font: { size: 14 }
          },
          ticks: { font: { size: 12 } }
        },
        y: {
          title: {
            display: true,
            text: "Average Incident Count",
            font: { size: 14 }
          },
          ticks: { font: { size: 12 } }
        }
      }
    }
  });
}
