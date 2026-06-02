import React from "react";
import { Button, Form, Space } from "antd";
import { SaveOutlined, UndoOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { buttonStyleBase } from "./formStyles";

const SaveButtons = ({ isChanged, handleReset, themeStyles }) => {
    const { t } = useTranslation();
    return (
        <Form.Item>
            <Space>
                <Button
                    type="primary"
                    htmlType="submit"
                    disabled={!isChanged}
                    style={{
                        ...buttonStyleBase,
                        background: themeStyles.primaryButton,
                        borderColor: themeStyles.primaryButton,
                    }}
                    icon={<SaveOutlined />}
                >
                    {t("save")}
                </Button>


                {isChanged && (
                    <Button danger onClick={handleReset} style={buttonStyleBase} icon={<UndoOutlined />}>
                        {t("cancel")}
                    </Button>
                )}
            </Space>
        </Form.Item>
    );
};

export default SaveButtons;