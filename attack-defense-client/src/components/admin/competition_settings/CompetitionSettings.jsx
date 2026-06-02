import React from "react";
import { Space } from "antd";
import { motion } from "framer-motion";
import { useTranslation } from "react-i18next";


import WhiteCardWithLabel from "../../common/WhiteCardWithLabel";
import CompetitionForm from "./CompetitionForm";
import CompetitionModeSwitcher from "./CompetitionModeSwitcher";
import useCompetitionSettings from "./useCompetitionSettings";


const CompetitionSettings = () => {
  const {
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
  } = useCompetitionSettings();

  const { t } = useTranslation();

  return (
      <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
      >
        <WhiteCardWithLabel
            title={t("competition_settings")}
            loading={loading}
            textColor={themeStyles.commonText}
            backgroundColor={themeStyles.window}
            button={<CompetitionModeSwitcher themeStyles={themeStyles} onModeChange={handleModeChange} isActive={formData.status !== "NEW"} />}
        >
          <Space direction="vertical" size="large" style={{ width: "100%" }}>
            <CompetitionForm
                formData={formData}
                handleChange={handleChange}
                handleSubmit={handleSubmit}
                handleReset={handleReset}
                isChanged={isChanged}
                disableStartDate={disableStartDate}
                disableEndDate={disableEndDate}
                textColor={themeStyles.commonText}
                backgroundColor={themeStyles.window}
                theme={theme}
                themeStyles={themeStyles}
                repoAction={repoAction}
                onRepoCreate={handleRepoCreate}
                onRepoSync={handleRepoSync}
            />
          </Space>
        </WhiteCardWithLabel>
      </motion.div>
  );
};

export default CompetitionSettings;