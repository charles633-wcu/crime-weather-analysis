function cToF(c) {
  return c * 9 / 5 + 32;
}

function safe(value, fallback) {
  if (value === null || value === undefined) return fallback;
  const v = String(value).trim();
  if (v === "" || v === "(null)" || v === "UNKNOWN") return fallback;
  return v;
}

function formatDailySummary(d) {
  return (
    `Date: ${d.date}\n` +
    `High Temperature: ${cToF(d.temperature_max).toFixed(1)}°F\n` +
    `Incident Count: ${d.incident_count}`
  );
}

function formatIncidents(incidents) {
  return incidents.map(i => {
    return (
      `• Borough: ${safe(i.borough, "Unknown Borough")} | Precinct: ${safe(i.precinct, "N/A")}\n` +
      `  Victim: ${safe(i.vic_sex, "Sex Not Reported")} (${safe(i.vic_age_group, "Age Not Reported")}) | ` +
      `Perpetrator: ${safe(i.perp_sex, "Sex Not Reported")} (${safe(i.perp_age_group, "Age Not Reported")})\n`
    );
  }).join("\n");
}

async function search() {

  const API_BASE = "http://54.173.129.38:9080";

  const date = document.getElementById("dateInput").value;
  if (!date) return;

  const day = window.SUMMARY_DATA.find(d => d.date === date);
  document.getElementById("summary").textContent =
    day ? formatDailySummary(day) : "No summary found for this date.";

  try {
    const res = await fetch(`${API_BASE}/api/incidents/by-date?date=${date}`);
    if (!res.ok) throw new Error();

    const incidents = await res.json();
    document.getElementById("incidents").textContent =
      incidents.length ? formatIncidents(incidents) : "No incidents recorded for this date.";
  } catch {
    document.getElementById("incidents").textContent = "Error retrieving incident data.";
  }
}

/**
 * Scatter-only filter.
 * Does not change global stats; it only changes which points are displayed
 * and updates the regression line endpoints to match the visible x-range.
 */
function applyTempFilter(range) {
  window.SCATTER_FILTER = range || "all";
  if (window.CHART_MODE === "scatter") {
    initChart();
  }
}
