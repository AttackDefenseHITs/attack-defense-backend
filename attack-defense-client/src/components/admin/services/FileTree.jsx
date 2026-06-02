import React, { useEffect, useState } from "react";
import { Tree, Spin, Typography, Divider, Col, Row } from "antd";
import {
    FolderOutlined,
    FileOutlined,
    FolderOpenOutlined
} from "@ant-design/icons";

import './styles/FileTree.module.css';

import {
    axiosGetCheckerFilesRoot,
    axiosGetCheckerFilesByPath,
    axiosGetCheckerFileContent
} from "../../../api/requests/checkerFilesRequests";

const { Text } = Typography;

const FileTree = ({ serviceId, onFileSelect }) => {

    const [treeData, setTreeData] = useState([]);
    const [loading, setLoading] = useState(false);

    // --- CACHES ---
    const [dirCache, setDirCache] = useState({});
    const [fileCache, setFileCache] = useState({});

    const [activeKey, setActiveKey] = useState(null);

    useEffect(() => {
        loadRoot(true);
    }, [serviceId]);

    /** Map backend to Ant Tree */
    const mapNodes = (nodes) =>
        nodes.map((n) => ({
            key: n.path,
            title: (
                <span className={activeKey === n.path ? "ft-active" : ""}>
                    {n.name}
                </span>
            ),
            isLeaf: !n.directory,
            icon: n.directory ? <FolderOutlined /> : <FileOutlined />
        }));

    /** Load root */
    async function loadRoot(reset = false) {
        setLoading(true);

        if (!reset && dirCache["/"]) {
            setTreeData(dirCache["/"]);
            setLoading(false);
            return;
        }

        const res = await axiosGetCheckerFilesRoot(serviceId);
        const mapped = mapNodes(res.data);

        setDirCache((prev) => ({ ...prev, "/": mapped }));
        setTreeData(mapped);
        setLoading(false);
    }

    /** Lazy load folders */
    const loadData = async (node) => {
        if (node.isLeaf) return;

        if (dirCache[node.key]) {
            node.children = dirCache[node.key];
            setTreeData([...treeData]);
            return;
        }

        const res = await axiosGetCheckerFilesByPath(serviceId, node.key);
        const mapped = mapNodes(res.data);

        node.children = mapped;

        setDirCache((prev) => ({ ...prev, [node.key]: mapped }));
        setTreeData([...treeData]);
    };

    /** When selecting file */
    const onSelect = async (keys, info) => {
        const node = info.node;

        if (!node.isLeaf) return;

        setActiveKey(node.key);

        if (fileCache[node.key]) {
            onFileSelect(node.key, fileCache[node.key]);
            return;
        }

        const res = await axiosGetCheckerFileContent(serviceId, node.key);

        setFileCache((prev) => ({ ...prev, [node.key]: res.data }));
        onFileSelect(node.key, res.data);
    };

    return (
        <div className="ft-container">
            <Row justify="space-between" align="middle" className="ft-header">
                <Col>
                    <Text strong style={{ fontSize: 14, display: "flex", alignItems: "center", gap: 6 }}>
                        <FolderOpenOutlined style={{ fontSize: 16, paddingRight: 8 }} />
                        Файлы чекера
                    </Text>
                </Col>
            </Row>

            <Divider style={{ margin: "8px 0" }} />

            {loading ? (
                <div className="ft-center">
                    <Spin />
                </div>
            ) : (
                <Tree
                    className="ft-tree"
                    showIcon
                    onSelect={onSelect}
                    loadData={loadData}
                    treeData={treeData}
                />
            )}
        </div>
    );
};

export default FileTree;
