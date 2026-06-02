import React, { useEffect, useState } from "react";
import { Segmented, message, Tooltip } from "antd";
import { ThunderboltOutlined, SyncOutlined } from "@ant-design/icons";
import {
    axiosGetCompetitionMode,
    axiosSetCompetitionMode,
} from "../../../../api/requests/competitionModeRequests.js";
import styles from "./CompetitionModeSwitcher.module.css";

const MODES = {
    ATTACK_DEFENSE: "ATTACK_DEFENSE",
    REVERSE_DEFENSE: "REVERSE_DEFENSE",
};

const MODE_LABELS = {
    ATTACK_DEFENSE: "Attack-Defense",
    REVERSE_DEFENSE: "Service Rescue",
};

const LABEL_TO_MODE = {
    [MODE_LABELS.ATTACK_DEFENSE]: MODES.ATTACK_DEFENSE,
    [MODE_LABELS.REVERSE_DEFENSE]: MODES.REVERSE_DEFENSE,
};

const CompetitionModeSwitcher = ({ themeStyles, onModeChange, isActive = false }) => {
    const [mode, setMode] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadMode = async () => {
            try {
                const response = await axiosGetCompetitionMode();
                setMode(response?.data?.competitionMode ?? MODES.ATTACK_DEFENSE);
            } catch (e) {
                message.error("Не удалось загрузить режим соревнования");
                setMode(MODES.ATTACK_DEFENSE);
            } finally {
                setLoading(false);
            }
        };
        loadMode();
    }, []);

    const handleChange = async (value) => {
        if (isActive) {
            message.warning("Нельзя менять режим во время соревнования");
            return;
        }

        const newMode = LABEL_TO_MODE[value];

        try {
            await axiosSetCompetitionMode(newMode);
            setMode(newMode);
            onModeChange?.(newMode);
            message.success("Режим соревнования изменён");
        } catch (e) {
            message.error("Ошибка смены режима");
        }
    };

    const selectedLabel = MODE_LABELS[mode] ?? MODE_LABELS.ATTACK_DEFENSE;
    const disabled = loading || isActive;

    const segmented = (
        <Segmented
            className={styles.segmentedOverride}
            size="large"
            value={selectedLabel}
            onChange={handleChange}
            disabled={disabled}
            style={{
                "--active-bg": themeStyles.primaryButton,
                "--active-text": themeStyles.primaryButtonText,
                background: themeStyles.window,
                border: `1px solid ${themeStyles.border}`,
                boxShadow: themeStyles.cardShadow,
                borderRadius: 8,
                padding: 2,
                opacity: disabled ? 0.7 : 1,
            }}
            options={[
                {
                    label: (
                        <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                            <ThunderboltOutlined /> {MODE_LABELS.ATTACK_DEFENSE}
                        </div>
                    ),
                    value: MODE_LABELS.ATTACK_DEFENSE,
                },
                {
                    label: (
                        <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                            <SyncOutlined /> {MODE_LABELS.REVERSE_DEFENSE}
                        </div>
                    ),
                    value: MODE_LABELS.REVERSE_DEFENSE,
                },
            ]}
        />
    );

    return isActive ? (
        <Tooltip title="Нельзя менять режим во время соревнования">
            <span style={{ display: "inline-block" }}>{segmented}</span>
        </Tooltip>
    ) : (
        segmented
    );
};

export default CompetitionModeSwitcher;