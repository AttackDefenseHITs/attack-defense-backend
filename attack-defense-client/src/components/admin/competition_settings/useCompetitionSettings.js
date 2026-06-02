import { useContext, useEffect, useMemo, useState } from "react";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import { message } from "antd";
import { useTranslation } from "react-i18next";

import ThemeContext from "../../../context/ThemeContext";
import {
    axiosGetCompetitionSettingsExtended,
    axiosUpdateCompetitionSettings,
    axiosPostRepoSync,
    axiosPostRepoCreate
} from "../../../api/requests/competitionRequests.js";

dayjs.extend(utc);
dayjs.extend(isSameOrBefore);

export const defaultData = {
    name: "",
    status: "NEW",
    startDate: null,
    endDate: null,
    totalRounds: 100,
    roundDurationMinutes: 3,
    rules: "",
    flagLostCost: 1,
    flagSendCost: 100,
    competitionMode: "ATTACK_DEFENSE",
    attackBotSettings: {
        enabled: false,
        attackProbability: 0.5,
        maxTargets: 1,
        cooldownRounds: 1,
        priorityScoreWeight: 1,
        prioritySlaWeight: 1,
        roundIntervalSeconds: 30,
    },
    repoUrl: "",
};

const updateByPath = (obj, path, value) => {
    if (!path) return obj;


    const keys = path.split(".");
    const result = { ...obj };
    let current = result;


    keys.forEach((key, index) => {
        if (index === keys.length - 1) {
            current[key] = value;
        } else {
            current[key] = { ...(current[key] || {}) };
            current = current[key];
        }
    });


    return result;
};

const useCompetitionSettings = () => {
    const { t } = useTranslation();
    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];


    const [formData, setFormData] = useState(defaultData);
    const [originalData, setOriginalData] = useState(null);
    const [loading, setLoading] = useState(true);


    const isChanged = useMemo(
        () => (originalData ? JSON.stringify(formData) !== JSON.stringify(originalData) : false),
        [formData, originalData]
    );

    const [repoAction, setRepoAction] = useState({
        creating: false,
        syncing: false,
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axiosGetCompetitionSettingsExtended();
                const data = response?.data;


                if (data) {
                    const loaded = {
                        name: data.name || "",
                        status: data.status ?? "NEW",
                        startDate: data.startDate ? dayjs.utc(data.startDate).local() : null,
                        endDate: data.endDate ? dayjs.utc(data.endDate).local() : null,
                        totalRounds: data.totalRounds,
                        roundDurationMinutes: data.roundDurationMinutes,
                        rules: data.rules || "",
                        flagLostCost: data.flagLostCost,
                        flagSendCost: data.flagSendCost,
                        competitionMode: data.competitionMode || "ATTACK_DEFENSE",
                        attackBotSettings: {
                            ...defaultData.attackBotSettings,
                            ...(data.attackBotSettings || {}),
                        },
                        repoUrl: data.repoUrl || "",
                    };


                    setFormData(loaded);
                    setOriginalData(loaded);
                }
            } catch (error) {
                message.error(t("competition_settings_load_error"));
                console.error("Error fetching competition settings:", error);
            } finally {
                setLoading(false);
            }
        };


        fetchData();
    }, [t]);

    const refetchSettings = async () => {
        const response = await axiosGetCompetitionSettingsExtended();
        const data = response?.data;
        if (!data) return;

        const loaded = {
            name: data.name || "",
            status: data.status ?? "NEW",
            startDate: data.startDate ? dayjs.utc(data.startDate).local() : null,
            endDate: data.endDate ? dayjs.utc(data.endDate).local() : null,
            totalRounds: data.totalRounds,
            roundDurationMinutes: data.roundDurationMinutes,
            rules: data.rules || "",
            flagLostCost: data.flagLostCost,
            flagSendCost: data.flagSendCost,
            competitionMode: data.competitionMode || "ATTACK_DEFENSE",
            attackBotSettings: {
                ...defaultData.attackBotSettings,
                ...(data.attackBotSettings || {}),
            },
            repoUrl: data.repoUrl || "",
        };

        setFormData(loaded);
        setOriginalData(loaded);
    };

    const handleChange = (fieldPath, value) => {
        let newData = updateByPath(formData, fieldPath, value ?? null);
        if (fieldPath === "startDate" && newData.endDate && value) {
            if (dayjs(value).isAfter(dayjs(newData.endDate))) {
                newData = { ...newData, endDate: null };
                message.info(t("end_date_reset_message"));
            }
        }
        setFormData(newData);
    };

    const handleReset = () => {
        if (originalData) {
            setFormData(originalData);
        } else {
            setFormData(defaultData);
        }
        message.info(t("form_reset_message"));
    };

    const handleSubmit = async () => {
        try {
            const payload = {
                ...formData,
                startDate: formData.startDate ? formData.startDate.toISOString() : null,
                endDate: formData.endDate ? formData.endDate.toISOString() : null,
            };

            await axiosUpdateCompetitionSettings(payload);
            message.success(t("competition_settings_save_success"));

            setOriginalData(formData);
        } catch (error) {
            message.error(t("competition_settings_save_error"));
            console.error("Error saving competition settings:", error);
        }
    };

    const handleRepoCreate = async () => {
        if (repoAction.creating || repoAction.syncing) return;

        setRepoAction((s) => ({ ...s, creating: true }));
        const hide = message.loading(t("repo_creating") || "Создаём репозиторий...", 0);

        try {
            await axiosPostRepoCreate();
            hide();
            message.success(t("repo_created") || "Репозиторий создан");
            await refetchSettings();
        } catch (e) {
            hide();
            message.error(t("repo_create_error") || "Ошибка создания репозитория");
            console.error("repo create error:", e);
        } finally {
            setRepoAction((s) => ({ ...s, creating: false }));
        }
    };

    const handleRepoSync = async () => {
        if (!formData.repoUrl) {
            message.info(t("repo_not_configured") || "Репозиторий ещё не создан");
            return;
        }
        if (repoAction.creating || repoAction.syncing) return;

        setRepoAction((s) => ({...s, syncing: true}));
        const hide = message.loading(t("repo_syncing") || "Синхронизация репозитория...", 0);

        try {
            await axiosPostRepoSync();
            hide();
            message.success(t("repo_synced") || "Репозиторий синхронизирован");
            await refetchSettings();
        } catch (e) {
            hide();
            message.error(t("repo_sync_error") || "Ошибка синхронизации репозитория");
            console.error("repo sync error:", e);
        } finally {
            setRepoAction((s) => ({...s, syncing: false}));
        }
    };

    const disableStartDate = (current) => {
        return current && current.isBefore(dayjs().startOf("day"));
    };

    const disableEndDate = (current) => {};

    const handleModeChange = (newMode) => {
        setFormData((prev) => ({
            ...prev,
            competitionMode: newMode,
            attackBotSettings:
                newMode === "ATTACK_DEFENSE"
                    ? prev.attackBotSettings
                    : { ...prev.attackBotSettings, enabled: false },
        }));
    };

    return {
        formData,
        loading,
        isChanged,
        theme,
        themeStyles,
        handleChange,
        handleSubmit,
        handleReset,
        disableStartDate,
        disableEndDate,
        handleModeChange,
        repoAction,
        handleRepoCreate,
        handleRepoSync,
    };
};

export default useCompetitionSettings;