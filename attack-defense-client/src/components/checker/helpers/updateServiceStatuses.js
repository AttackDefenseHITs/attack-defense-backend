export const updateServiceStatuses = (prevData, newStatuses) => {
    const prevDataMap = new Map(prevData.map((team) => [team.team.id, team]));
  
    newStatuses.forEach((newStatus) => {
      if (prevDataMap.has(newStatus.team.id)) {
        const existingTeam = prevDataMap.get(newStatus.team.id);
  
        prevDataMap.set(newStatus.team.id, {
          ...existingTeam,
          services: {
            ...existingTeam.services,
            ...newStatus.services,
          },
        });
      }
    });
  
    return Array.from(prevDataMap.values());
  };
  