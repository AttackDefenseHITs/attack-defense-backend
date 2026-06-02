import React from "react";
import { useTranslation } from "react-i18next";
import { Avatar, Row, Col, Typography } from "antd";
import { TrophyFilled, StarFilled } from "@ant-design/icons";

import styles from "./styles/TeamInfoBox.module.css";

const { Text } = Typography;

const TeamInfoBox = ({ team, themeStyles }) => {
  const { t } = useTranslation();

  const placeColors = {
    1: "#FFD700",
    2: "#C0C0C0",
    3: "#CD7F32",
    default: themeStyles.primaryButton,
  };

  const placeColor = placeColors[team.place] || placeColors.default;

  return (
      <div
          className={styles.wrapper}
          style={{
            backgroundColor: themeStyles.contentBackground,
            flexShrink: 0,
            width: "fit-content",
          }}
      >
        <Row align="middle" gutter={12} wrap={false}>
          <Col flex="none">
            <div className={styles.cell}>
              <span className={styles.label}>{t("place")}</span>
              <div className={styles.valueRow}>
                <TrophyFilled className={styles.icon} style={{ color: placeColor }} />
                <Avatar
                    size={24}
                    style={{
                      backgroundColor: placeColor,
                      color: "#fff",
                      fontWeight: 600,
                      fontSize: 16,
                    }}
                >
                  {team.place}
                </Avatar>
              </div>
            </div>
          </Col>

          <Col flex="none">
            <div className={styles.separator} />
          </Col>

          <Col flex="none">
            <div className={styles.cell}>
              <span className={styles.label}>{t("score")}</span>
              <div className={styles.valueRow}>
                <StarFilled className={styles.icon} style={{ color: placeColors[1] }} />
                <Text strong className={styles.score} style={{ color: themeStyles.commonText, fontSize: 18 }}>
                  {team.points}
                </Text>
              </div>
            </div>
          </Col>
        </Row>
      </div>
  );
};

export default TeamInfoBox;