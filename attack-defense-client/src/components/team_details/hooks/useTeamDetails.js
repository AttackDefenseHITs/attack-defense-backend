import { useEffect, useState } from 'react';
import { axiosGetTeamById } from '../../../api/requests/getTeamByIdRequest.js';

const useTeamDetails = (teamId) => {
    const [team, setTeam] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        let mounted = true;

        const fetchTeam = async () => {
            setLoading(true);
            setError(false);

            try {
                const response = await axiosGetTeamById(teamId);
                if (mounted) setTeam(response.data);
            } catch {
                if (mounted) setError(true);
            } finally {
                if (mounted) setLoading(false);
            }
        };

        fetchTeam();
        return () => (mounted = false);
    }, [teamId]);

    return { team, setTeam, loading, error };
};

export default useTeamDetails;
