export const getActionLabels = (t) => ({
    START: t("start_competition"),
    COMPLETE: t("complete_competition"),
    CANCEL: t("cancel_competition"),
    PAUSE: t("pause_competition"),
    RESUME: t("resume_competition"),
});

export const getStatusLabels = (t) => ({
    NEW: t("competition_not_started"),
    IN_PROGRESS: t("competition_in_progress"),
    COMPLETED: t("competition_completed"),
    CANCELLED: t("competition_cancelled"),
    PAUSED: t("competition_paused"),
});

const StatusColors = {
    NEW: 'blue',
    IN_PROGRESS: 'green',
    COMPLETED: 'gray',
    CANCELLED: 'red',
    PAUSED: 'orange',
};

export const getActionLabel = (action, t) => getActionLabels(t)[action] || t("unknown_action");
export const getStatusLabel = (status, t) => getStatusLabels(t)[status] || t("unknown_status");
export const getStatusColor = (status) => StatusColors[status] || "gray";
