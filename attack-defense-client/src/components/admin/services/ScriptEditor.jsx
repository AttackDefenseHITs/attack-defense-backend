import React from 'react';
import { Editor } from '@monaco-editor/react';

const ScriptEditor = ({ serviceId, serviceScripts, setServiceScripts, theme }) => {
    return (
        <div style={{ marginTop: 8, border: '1px solid #d9d9d9', borderRadius: 4 }}>
            <Editor
                height="400px"
                language="python"
                theme={theme === 'dark' ? 'vs-dark' : 'vs-light'}
                value={serviceScripts[serviceId] || ""}
                onChange={(v) =>
                    setServiceScripts((prev) => ({ ...prev, [serviceId]: v }))
                }
                options={{
                    minimap: { enabled: true },
                    scrollBeyondLastLine: false,
                    automaticLayout: true,
                }}
            />
        </div>
    );
};

export default ScriptEditor;
