import React, { useEffect, useState, useContext } from "react";
import { Button, Tag, Skeleton } from "antd";
import { DownOutlined, TrophyOutlined } from "@ant-design/icons";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import duration from "dayjs/plugin/duration";
import { getStatusLabel, getStatusColor } from "../../utils/competitionMapper";
import TimeDisplay from "../common/TimeDisplay";
import BoldText from "../common/BoldText";
import { useTranslation } from "react-i18next";
import ThemeContext from "../../context/ThemeContext";

dayjs.extend(utc);
dayjs.extend(duration);

const calculateTimeLeft = (targetDate) => {
  if (!targetDate) return null;
  const now = dayjs();
  const target = dayjs.utc(targetDate).local();
  return target.diff(now, "seconds");
};

const EventBanner = ({
  title,
  startTime,
  endTime,
  status,
  scrollToTeams,
  currentRound,
  loading,
}) => {
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const [startTimeLeft, setStartTimeLeft] = useState(null);
  const [endTimeLeft, setEndTimeLeft] = useState(null);

  const themeStyles = themeConfig[theme];

  useEffect(() => {
    const updateTimers = () => {
      if (status === "NEW" && startTime) {
        setStartTimeLeft(calculateTimeLeft(startTime));
      } else if (status === "IN_PROGRESS" && endTime) {
        setEndTimeLeft(calculateTimeLeft(endTime));
      }
    };

    updateTimers();
    const interval = setInterval(updateTimers, 1000);

    return () => clearInterval(interval);
  }, [startTime, endTime, status]);

  const renderStatusTimer = () => {
    switch (status) {
      case "NEW":
        return startTime ? (
          <TimeDisplay
            label={t("start")}
            timeLeft={startTimeLeft}
            targetDate={startTime}
            showCountdown={startTimeLeft <= 86400}
            color={themeStyles.text}
          />
        ) : (
          <BoldText text={t("start_coming_soon")} color={themeStyles.text} />
        );

      case "IN_PROGRESS":
        return endTime ? (
          <div>
            <TimeDisplay
              label={t("end")}
              timeLeft={endTimeLeft}
              targetDate={endTime}
              showCountdown={endTimeLeft <= 86400}
              color={themeStyles.text}
            />
            <div className="round-indicator">
              <TrophyOutlined style={{ marginRight: '8px', color: themeStyles.highlight }} />
              <BoldText text={`${t("current_round")}: ${currentRound}`} color={themeStyles.text} />
            </div>
          </div>
        ) : (
          <div>
            <BoldText text={t("end_coming_soon")} color={themeStyles.text} />
            <div className="round-indicator">
              <TrophyOutlined style={{ marginRight: '8px', color: themeStyles.highlight }} />
              <BoldText text={`${t("current_round")}: ${currentRound}`} color={themeStyles.text} />
            </div>
          </div>
        );

      default:
        return "";
    }
  };

  return (
    <div
      className="event-banner"
      style={{
        position: "relative",
        backgroundSize: "cover",
        backgroundPosition: "center",
        height: "400px",
        // marginBottom: "40px",
        overflow: "hidden",
      }}
    >
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          background: themeStyles.bannerGradient,
          zIndex: 1,
        }}
      ></div>

      <div
        className="custom-card fade-in"
        style={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          backgroundColor: themeStyles.cardBackground,
          color: themeStyles.commonText,
          padding: "30px 40px",
          borderRadius: "16px",
          boxShadow: themeStyles.cardShadow,
          zIndex: 2,
          textAlign: "center",
          minWidth: "450px",
          maxWidth: "90%",
          backdropFilter: "blur(10px)",
          border: `1px solid ${themeStyles.cardBorder}`,
        }}
      >
        {loading ? (
          <>
            <Skeleton.Input
              active
              style={{ width: 200, height: 32, marginBottom: 20 }}
            />
            <Skeleton.Button
              active
              style={{ width: 80, height: 24, marginBottom: 20 }}
            />
            <Skeleton
              active
              paragraph={{ rows: 2, width: "80%" }}
              style={{ marginBottom: 20 }}
            />
            <Skeleton.Button active style={{ width: 150, height: 40 }} />
          </>
        ) : (
          <>
            <h1
              className="slide-up"
              style={{
                fontSize: "42px",
                margin: 0,
                color: themeStyles.commonText,
                fontWeight: "bold",
                marginBottom: "16px",
                letterSpacing: "-0.5px",
              }}
            >
              {title}
            </h1>
            <div 
              className="slide-up" 
              style={{ 
                marginTop: "10px", 
                marginBottom: "20px",
                animation: "slideUp 0.5s ease-out 0.2s both" 
              }}
            >
              <Tag
                color={getStatusColor(status)}
                style={{ 
                  fontSize: "16px", 
                  padding: "6px 12px", 
                  borderRadius: "20px",
                  fontWeight: "500"
                }}
              >
                {getStatusLabel(status, t)}
              </Tag>
            </div>
            <div 
              className="slide-up" 
              style={{ 
                marginBottom: "24px",
                animation: "slideUp 0.5s ease-out 0.4s both" 
              }}
            >
              {renderStatusTimer()}
            </div>
            <Button
              type="primary"
              size="large"
              onClick={scrollToTeams}
              className="custom-button slide-up"
              style={{
                backgroundColor: themeStyles.primaryButton,
                borderColor: themeStyles.primaryButton,
                color: themeStyles.primaryButtonText,
                borderRadius: "8px",
                height: "44px",
                padding: "0 24px",
                fontSize: "16px",
                fontWeight: "500",
                animation: "slideUp 0.5s ease-out 0.6s both"
              }}
              icon={<DownOutlined />}
            >
              {t("go_to_teams")}
            </Button>
          </>
        )}
      </div>

      <style jsx>{`
        .round-indicator {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-top: 12px;
          padding: 8px 16px;
          background-color: ${theme === 'dark' ? 'rgba(255, 255, 255, 0.05)' : 'rgba(0, 0, 0, 0.03)'};
          border-radius: 8px;
          display: inline-flex;
        }
        
        @media (max-width: 768px) {
          .event-banner {
            height: 350px;
          }
        }
        
        @media (max-width: 480px) {
          .event-banner {
            height: 300px;
          }
        }
      `}</style>
    </div>
  );
};

export default EventBanner;
