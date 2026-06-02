import React from "react";
import { Card, Divider, Typography } from "antd";
import TeamActions from "../TeamActions.jsx";
import TeamMembersTable from "../TeamMembersTable.jsx";
import TeamInfoBox from "../TeamInfoBox.jsx";

const { Title, Text } = Typography;

const TeamContentCard = ({ team, setTeam, themeStyles, t, navigate }) => (
    <Card
        className="custom-card fade-in"
        style={{
            flex: 3,
            borderRadius: "16px",
            boxShadow: themeStyles.cardShadow,
            padding: "20px",
            backgroundColor: themeStyles.cardBackground,
            color: themeStyles.commonText,
            border: `1px solid ${themeStyles.cardBorder}`,
        }}
    >
        <div
            style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 16,
                flexWrap: "nowrap",
            }}
        >
            <div style={{ flex: 1, minWidth: 0 }}>
                <Title
                    level={3}
                    style={{
                        marginBottom: 0,
                        color: themeStyles.commonText,
                        whiteSpace: "nowrap",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                    }}
                >
                    {team.name}
                </Title>

                <Text style={{ color: themeStyles.lightText, fontSize: 16 }}>
                    {t("members")}: {team.userCount}/{team.membersCount}
                </Text>
            </div>

            <div style={{ flex: "none", marginLeft: "auto" }}>
                <TeamInfoBox team={team} themeStyles={themeStyles} />
            </div>
        </div>

        <Divider style={{ borderColor: themeStyles.border, margin: "24px 0" }} />

        <TeamActions
            team={team}
            setTeam={setTeam}
            navigate={navigate}
            defaultButtonColor={themeStyles.cardBackground}
            textColor={themeStyles.commonText}
        />

        <Divider style={{ borderColor: themeStyles.border, margin: "24px 0" }} />

        <TeamMembersTable members={team.memberList} themeStyles={themeStyles} pageSize={5} />
    </Card>
);

export default TeamContentCard;