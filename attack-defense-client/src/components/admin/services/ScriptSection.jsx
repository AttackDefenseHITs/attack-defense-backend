import React, { useState } from 'react';
import { Button, Row, Col, Typography } from 'antd';
import { SaveOutlined } from '@ant-design/icons';
import ScriptEditor from './ScriptEditor';
import FileTree from './FileTree';

const { Text } = Typography;

const ScriptSection = ({
                           serviceId,
                           handleSaveScript,
                           serviceScripts,
                           setServiceScripts,
                           theme,
                           backgroundColor,
                           buttonColor,
                           buttonText,
                           t
                       }) => {
    const [selectedPath, setSelectedPath] = useState("run.py");

    const handleFileSelect = (path, content) => {
        setSelectedPath(path);
        setServiceScripts((prev) => ({
            ...prev,
            [serviceId]: content
        }));
    };

    return (
        <div style={{ backgroundColor, borderRadius: '8px' }}>
            <Row>
                <Col span={4}>
                    <FileTree serviceId={serviceId} onFileSelect={handleFileSelect} />
                </Col>
                <Col span={20}>
                    {selectedPath && (
                        <Text strong style={{ marginBottom: 8, display: "block" }}>
                            {selectedPath}
                        </Text>
                    )}

                    <ScriptEditor
                        serviceId={serviceId}
                        serviceScripts={serviceScripts}
                        setServiceScripts={setServiceScripts}
                        theme={theme}
                    />

                    <Button
                        type="primary"
                        icon={<SaveOutlined />}
                        style={{
                            marginTop: 16,
                            backgroundColor: buttonColor,
                            color: buttonText,
                            borderColor: buttonColor
                        }}
                        onClick={() => handleSaveScript(serviceId)}
                    >
                        {t("save_script")}
                    </Button>
                </Col>
            </Row>
        </div>
    );
};

export default ScriptSection;
