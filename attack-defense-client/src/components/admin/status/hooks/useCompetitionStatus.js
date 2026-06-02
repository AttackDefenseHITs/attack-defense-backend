import { useEffect, useState } from 'react';
import { message } from 'antd';
import {
    axiosGetCompetitionSettings,
    axiosChangeCompetitionStatus,
    axiosGetAvailableCompetitionActions,
    axiosPostRestartCompetition,
} from '../../../../api/requests/competitionRequests';

export const useCompetitionStatus = (t) => {
    const [status, setStatus] = useState(null);
    const [availableActions, setAvailableActions] = useState([]);
    const [loading, setLoading] = useState(true);

    const fetchCompetitionData = async () => {
        setLoading(true);
        try {
            const [settings, actions] = await Promise.all([
                axiosGetCompetitionSettings(),
                axiosGetAvailableCompetitionActions(),
            ]);

            setStatus(settings?.data?.status || 'UNKNOWN');
            setAvailableActions(actions?.data || []);
        } catch {
            message.error(t('competition_load_error'));
        } finally {
            setLoading(false);
        }
    };

    const changeStatus = async (action) => {
        try {
            await axiosChangeCompetitionStatus({ action });
            message.success(t('success_save'));
            await fetchCompetitionData();
        } catch {
            message.error(t('action_error'));
        }
    };

    const restartCompetition = async () => {
        try {
            await axiosPostRestartCompetition();
            message.success(t('restart_success'));
            await fetchCompetitionData();
        } catch {
            message.error(t('restart_error'));
        }
    };

    useEffect(() => {
        fetchCompetitionData();
    }, []);

    return {
        status,
        availableActions,
        loading,
        changeStatus,
        restartCompetition,
    };
};
