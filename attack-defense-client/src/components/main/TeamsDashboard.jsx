import { Line } from "react-chartjs-2";
import "chart.js/auto";
import { useTranslation } from "react-i18next";
import { useContext, useRef, useState } from "react";
import ThemeContext from "../../context/ThemeContext";
import { Chart } from "chart.js";
import zoomPlugin from "chartjs-plugin-zoom";
import "chartjs-adapter-date-fns";
import { ru } from "date-fns/locale";

Chart.register(zoomPlugin);

const TeamsDashboard = ({ chartData, onExpand, hideExpand = false }) => {
  const { t } = useTranslation();
  const { theme, themeConfig } = useContext(ThemeContext);
  const themeStyles = themeConfig[theme];
  const chartRef = useRef(null);
  const [zoomLocked, setZoomLocked] = useState(true);

  const toggleZoomLock = () => {
    const chart = chartRef.current;
    if (chart) {
      const newZoomState = !zoomLocked;
      chart.options.plugins.zoom.zoom.wheel.enabled = !newZoomState;
      chart.update();
      setZoomLocked(newZoomState);
    }
  };

  return (
      <div
          style={{
            width: "100%",
            minHeight: "400px",
            maxHeight: "500px",
            padding: "10px",
            color: themeStyles.text,
            borderRadius: "8px",
            boxSizing: "border-box",
            position: "relative",
          }}
      >
        {/* Expand button */}
        {!hideExpand && typeof onExpand === "function" && (
            <button
                onClick={onExpand}
                style={{
                  position: "absolute",
                  top: "10px",
                  right: "52px",
                  background: themeStyles.background,
                  color: themeStyles.text,
                  border: "1px solid",
                  borderColor: theme === "dark" ? "#555" : "#ccc",
                  borderRadius: "6px",
                  padding: "6px 10px",
                  cursor: "pointer",
                  zIndex: 10,
                }}
                title={t("expand_chart")}
            >
              ⤢
            </button>
        )}

        {/* Lock zoom */}
        <button
            onClick={toggleZoomLock}
            style={{
              position: "absolute",
              top: "10px",
              right: "10px",
              background: themeStyles.background,
              color: themeStyles.text,
              border: "1px solid",
              borderColor: theme === "dark" ? "#555" : "#ccc",
              borderRadius: "6px",
              padding: "6px 10px",
              cursor: "pointer",
              zIndex: 10,
            }}
            title={zoomLocked ? t("zoomLocked") : t("zoomUnlocked")}
        >
          {zoomLocked ? "🔒" : "🔓"}
        </button>

        <Line
            ref={chartRef}
            data={chartData}
            options={{
              responsive: true,
              maintainAspectRatio: false,
              interaction: { intersect: false, mode: "nearest" },
              plugins: {
                legend: { position: "bottom", labels: { color: themeStyles.text } },
                tooltip: {
                  mode: "index",
                  intersect: false,
                  backgroundColor: themeStyles.background,
                  titleColor: themeStyles.text,
                  bodyColor: themeStyles.text,
                  callbacks: {
                    label: (tooltipItem) => {
                      const dataset = tooltipItem.dataset;
                      const currentValue = tooltipItem.raw?.y ?? NaN;
                      const previousValue = dataset.data[tooltipItem.dataIndex - 1]?.y ?? 0;
                      const difference = currentValue - previousValue;
                      return t("score_delta_tooltip", {
                        label: dataset.label,
                        sign: currentValue > previousValue ? "+" : "",
                        difference,
                        score: t("score"),
                        currentValue,
                      });
                    },
                  },
                },
                zoom: {
                  zoom: {
                    wheel: { enabled: !zoomLocked },
                    pinch: { enabled: true },
                    mode: "xy",
                  },
                  pan: { enabled: true, mode: "xy" },
                  limits: {
                    x: { min: "original", max: "original" },
                    y: { min: "original", max: "original" },
                  },
                },
                decimation: { enabled: true, algorithm: "lttb", samples: 50, threshold: 100 },
              },
              scales: {
                x: {
                  type: "time",
                  time: {
                    unit: "minute",
                    stepSize: 1,
                    displayFormats: {
                      millisecond: "HH:mm:ss",
                      second: "HH:mm:ss",
                      minute: "HH:mm",
                      hour: "HH:mm",
                      day: "dd MMM",
                      week: "dd MMM",
                      month: "MMM yyyy",
                      year: "yyyy",
                    },
                    tooltipFormat: "dd MMM yyyy HH:mm",
                  },
                  adapters: { date: { locale: ru } },
                  title: { display: true, text: t("time"), color: themeStyles.text },
                  ticks: {
                    autoSkip: true,
                    maxRotation: 45,
                    minRotation: 45,
                    color: themeStyles.text,
                    maxTicksLimit: 10,
                  },
                  grid: {
                    color: theme === "dark" ? "rgba(255, 255, 255, 0.2)" : "rgba(0, 0, 0, 0.1)",
                  },
                },
                y: {
                  title: { display: true, text: t("score"), color: themeStyles.text },
                  ticks: { color: themeStyles.text },
                  grid: {
                    color: theme === "dark" ? "rgba(255, 255, 255, 0.2)" : "rgba(0, 0, 0, 0.1)",
                  },
                  suggestedMin: 0,
                },
              },
              elements: {
                line: { tension: 0.3, borderWidth: 2, fill: false },
                point: { radius: 3, hoverRadius: 6, hitRadius: 10 },
              },
              spanGaps: true,
            }}
        />
      </div>
  );
};

export default TeamsDashboard;
