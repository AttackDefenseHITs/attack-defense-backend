import React from "react";
import { Form } from "antd";
import { motion } from "framer-motion";

import MainSettings from "./MainSettings";
import RepositorySettings from "./RepositorySettings";
import AttackBotSettings from "./AttackBotSettings";
import SaveButtons from "./SaveButtons";

const CompetitionForm = ({
                             formData,
                             handleChange,
                             handleSubmit,
                             handleReset,
                             isChanged,
                             disableStartDate,
                             disableEndDate,
                             textColor,
                             backgroundColor,
                             theme,
                             themeStyles,
                             repoAction,
                             onRepoCreate,
                             onRepoSync,
                         }) => {
    return (
        <>
            <style>{`
                .custom-number .ant-input-number-input {
                height: 30px !important;
                line-height: 30px !important;
                padding: 0 8px !important;
                }
            `}</style>


            <Form layout="vertical" initialValues={formData} onFinish={handleSubmit} size="small">
                <motion.div
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.3 }}
                >
                    <MainSettings
                        formData={formData}
                        handleChange={handleChange}
                        disableStartDate={disableStartDate}
                        disableEndDate={disableEndDate}
                        textColor={textColor}
                        backgroundColor={backgroundColor}
                        theme={theme}
                        themeStyles={themeStyles}
                    />

                    <RepositorySettings
                        formData={formData}
                        handleChange={handleChange}
                        textColor={textColor}
                        backgroundColor={backgroundColor}
                        theme={theme}
                        themeStyles={themeStyles}
                        repoAction={repoAction}
                        onRepoCreate={onRepoCreate}
                        onRepoSync={onRepoSync}
                    />

                    <AttackBotSettings
                        formData={formData}
                        handleChange={handleChange}
                        textColor={textColor}
                        backgroundColor={backgroundColor}
                        theme={theme}
                        themeStyles={themeStyles}
                    />
                    <SaveButtons isChanged={isChanged} handleReset={handleReset} themeStyles={themeStyles} />
                </motion.div>
            </Form>
        </>
    );
};

export default CompetitionForm;