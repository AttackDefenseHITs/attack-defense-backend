export const transformData = (rawData) => {
  const sortedData = [...rawData].sort(
    (a, b) => new Date(a.submissionTime) - new Date(b.submissionTime)
  );

  const teams = [...new Set(sortedData.map((entry) => entry.teamName))];

  const datasets = teams.map((team) => {
    const teamEntries = sortedData.filter((e) => e.teamName === team);

    const data = teamEntries.map((entry) => ({
      x: new Date(entry.submissionTime),
      y: entry.totalTeamPoints ?? NaN,
    }));

    const teamColor =
      teamEntries[0]?.color ||
      `#${Math.floor(Math.random() * 16777215).toString(16)}`;

    return {
      label: team,
      data,
      borderColor: teamColor,
      tension: 0.2,
      fill: false,
    };
  });

  return { datasets };
};
