import React from "react";
import { Card } from "antd";
import VirtualMachineInfo from "./VirtualMachineInfo.jsx";
import FlagSubmissionCard from "./FlagSubmissionCard.jsx";

const TeamRightColumn = ({ team, themeStyles, t, submitting, handleFlagSubmit }) => (
    <div style={{ flex: 2, display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <Card
            className="custom-card fade-in"
            style={{
                borderRadius: '16px',
                boxShadow: themeStyles.cardShadow,
                backgroundColor: themeStyles.cardBackground,
                color: themeStyles.commonText,
                border: `1px solid ${themeStyles.cardBorder}`,
                overflow: 'hidden',
            }}
            title={
                <div
                    style={{
                        color: themeStyles.commonText,
                        fontSize: '18px',
                        fontWeight: '500',
                        padding: '8px 0',
                    }}
                >
                    {t('vm_info')}
                </div>
            }
        >
            <VirtualMachineInfo
                virtualMachine={team.virtualMachine}
                textColor={themeStyles.commonText}
            />
        </Card>

        <Card
            className="custom-card fade-in"
            style={{
                borderRadius: '16px',
                boxShadow: themeStyles.cardShadow,
                backgroundColor: themeStyles.cardBackground,
                color: themeStyles.commonText,
                border: `1px solid ${themeStyles.cardBorder}`,
                overflow: 'hidden',
            }}
            title={
                <div
                    style={{
                        color: themeStyles.commonText,
                        fontSize: '18px',
                        fontWeight: '500',
                        padding: '8px 0',
                    }}
                >
                    {t('flag_submission')}
                </div>
            }
        >
            <FlagSubmissionCard
                handleFlagSubmit={handleFlagSubmit}
                submitting={submitting}
                textColor={themeStyles.commonText}
                backgroundColor={themeStyles.cardBackground}
                inputBackground={themeStyles.inputBackground}
                buttonColor={themeStyles.primaryButton}
                buttonTextColor={themeStyles.primaryButtonText}
            />
        </Card>
    </div>
);

export default TeamRightColumn;
