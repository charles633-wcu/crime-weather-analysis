function initChart() {
  if (!Array.isArray(window.SUMMARY_DATA) || !window.SUMMARY_DATA.length) {
    console.error("SUMMARY_DATA missing");
    return;
  }

  const ctx = document.getElementById("chart");

  const points = window.SUMMARY_DATA.map(d => ({
    x: d.temperature_max,
    y: d.incident_count
  }));

  const { alpha, beta } = window.ANALYTICS_DATA;

  const minX = Math.min(...points.map(p => p.x));
  const maxX = Math.max(...points.map(p => p.x));

  const regressionLine = [
    { x: minX, y: alpha + beta * minX },
    { x: maxX, y: alpha + beta * maxX }
  ];

  new Chart(ctx, {
    type: "scatter",
    data: {
      datasets: [
        {
          label: "Daily Incidents",
          data: points,
          backgroundColor: "steelblue"
        },
        {
          label: "Regression Line",
          type: "line",
          data: regressionLine,
          borderColor: "red",
          fill: false
        }
      ]
    },
    options: {
      scales: {
        x: { title: { display: true, text: "Max Temperature" }},
        y: { title: { display: true, text: "Incident Count" }}
      }
    }
  });
}
