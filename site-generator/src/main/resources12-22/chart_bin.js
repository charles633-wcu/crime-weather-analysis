function cToF(c) {
  return c * 9 / 5 + 32;
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

  const bins = [
    { label: "<30°F",   min: -100, max: 30,  values: [] },
    { label: "30–40°F", min: 30,   max: 40,  values: [] },
    { label: "40–50°F", min: 40,   max: 50,  values: [] },
    { label: "50–60°F", min: 50,   max: 60,  values: [] },
    { label: "60–70°F", min: 60,   max: 70,  values: [] },
    { label: "70–80°F", min: 70,   max: 80,  values: [] },
    { label: "80–90°F", min: 80,   max: 90,  values: [] },
    { label: ">90°F",   min: 90,   max: 200, values: [] }
  ];

  window.SUMMARY_DATA.forEach(d => {
    const tempF = cToF(d.temperature_max);
    const bin = bins.find(b => tempF >= b.min && tempF < b.max);
    if (bin) bin.values.push(d.incident_count);
  });

  const labels = bins.map(b => b.label);
  const averages = bins.map(b =>
    b.values.length ? (b.values.reduce((a, v) => a + v, 0) / b.values.length) : 0
  );

  window.currentChart = new Chart(ctx, {
    type: "bar",
    data: {
      labels,
      datasets: [{
        label: "Average Daily Incidents",
        data: averages,
        backgroundColor: "steelblue"
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,

      plugins: {
        title: {
          display: true,
          text: "Average Daily Shooting Incidents by Temperature Range",
          font: { size: 16 }
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
            text: "Average Incident Count",
            font: { size: 14 }
          }
        }
      }
    }
  });
}
