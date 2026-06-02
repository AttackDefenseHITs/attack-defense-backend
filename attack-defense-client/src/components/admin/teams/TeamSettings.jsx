import React, { useState, useEffect, useContext } from 'react';
import { Button, List, message, Input, Typography, Space, Avatar, Badge } from 'antd';
import { DeleteOutlined, PlusOutlined, EditOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';
import WhiteCardWithLabel from '../../common/WhiteCardWithLabel';
import CreateTeamsModal from './CreateTeamsModal';
import EditTeamModal from './EditTeamModal';
import { axiosGetAllTeams } from '../../../api/requests/getTeamsRequest';
import { 
    axiosCreateTeam, 
    axiosDeleteTeam, 
    axiosCreateManyTeams, 
    axiosUpdateTeam, 
} from '../../../api/requests/adminTeamsRequests';
import ThemeContext from "../../../context/ThemeContext";
import '../../styles/pagination.css';
import { useTranslation } from 'react-i18next';

const { Text } = Typography;

const TeamSettings = () => {
    const [teams, setTeams] = useState([]);
    const [filteredTeams, setFilteredTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isCreateModalVisible, setIsCreateModalVisible] = useState(false);
    const [isEditModalVisible, setIsEditModalVisible] = useState(false);
    const [editTeam, setEditTeam] = useState(null);
    const [searchText, setSearchText] = useState('');

    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];
    const { t } = useTranslation();

    useEffect(() => {
        fetchTeams();
    }, []);

    const fetchTeams = async () => {
        try {
            setLoading(true);
            const response = await axiosGetAllTeams();
            setTeams(response.data || []);
            setFilteredTeams(response.data || []);
        } catch (error) {
            message.error(t('teams_load_error'));
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (value) => {
        setSearchText(value);
        const filtered = teams.filter(team => 
            team.name.toLowerCase().includes(value.toLowerCase())
        );
        setFilteredTeams(filtered);
    };

    const handleAddTeam = async (values) => {
        try {
            if (values.creationMode === 'one') {
                const response = await axiosCreateTeam({ name: values.name, maxMembers: values.maxMembers });
                message.success(t('team_created_success'));
                setTeams((prevTeams) => [...prevTeams, response.data]);
                setFilteredTeams((prevTeams) => [...prevTeams, response.data]);
            } else if (values.creationMode === 'bulk') {
                await axiosCreateManyTeams({ teamsCount: values.teamsCount, maxMembers: values.maxMembers });
                message.success(t('teams_created_success'));
                fetchTeams();
            }
            setIsCreateModalVisible(false);
        } catch (error) {
            message.error(t('teams_create_error'));
        }
    };

    const handleEditTeam = async (values) => {
        try {
            await axiosUpdateTeam(editTeam.id, values);
            message.success(t('team_updated_success'));
            setTeams((prevTeams) =>
                prevTeams.map((team) => (team.id === editTeam.id ? { ...editTeam, ...values } : team))
            );
            setFilteredTeams((prevTeams) =>
                prevTeams.map((team) => (team.id === editTeam.id ? { ...editTeam, ...values } : team))
            );
            setIsEditModalVisible(false);
            setEditTeam(null);
        } catch (error) {
            message.error(t('team_update_error'));
        }
    };

    const handleDeleteTeam = async (teamId) => {
        try {
            await axiosDeleteTeam(teamId);
            message.success(t('team_deleted_success'));
            setTeams((prevTeams) => prevTeams.filter((team) => team.id !== teamId));
            setFilteredTeams((prevTeams) => prevTeams.filter((team) => team.id !== teamId));
        } catch (error) {
            message.error(t('team_delete_error'));
        }
    };

    const handleEditClick = (team) => {
        console.log("CLICK");
        setEditTeam(team);
        setIsEditModalVisible(true);
    };

    const getRandomColor = (teamName) => {
        const colors = [
            '#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1',
            '#13c2c2', '#eb2f96', '#fa8c16', '#a0d911', '#fadb14'
        ];
        
        const hash = teamName.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
        return colors[hash % colors.length];
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
        >
            <WhiteCardWithLabel
                title={t('team_settings')}
                loading={loading}
                textColor={themeStyles.commonText}
                backgroundColor={themeStyles.window}
                button={
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        style={{
                            backgroundColor: themeStyles.primaryButton,
                            color: themeStyles.primaryButtonText,
                            borderColor: themeStyles.primaryButton,
                            borderRadius: '4px',
                            height: '32px',
                            display: 'flex',
                            alignItems: 'center',
                            gap: '6px',
                            padding: '0 12px'
                        }}
                        size="small"
                        onClick={() => setIsCreateModalVisible(true)}
                    >
                        {t('add_team')}
                    </Button>
                }
            >
                <Space direction="vertical" size="large" style={{ width: '100%' }}>
                    <Input.Search
                        placeholder={t('teams_search')}
                        value={searchText}
                        onChange={(e) => handleSearch(e.target.value)}
                        allowClear
                        style={{ 
                            marginBottom: 12,
                            backgroundColor: themeStyles.inputBackground,
                            color: themeStyles.inputText,
                            borderColor: themeStyles.border,
                            borderRadius: '4px'
                        }}
                    />
                    <AnimatePresence>
                        <List
                            bordered={false}
                            dataSource={filteredTeams}
                            style={{
                                color: themeStyles.commonText,
                                borderColor: themeStyles.border
                            }}
                            renderItem={(team) => (
                                <motion.div
                                    key={team.id}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -10 }}
                                    transition={{ duration: 0.3 }}
                                >
                                    <List.Item
                                        style={{
                                            color: themeStyles.commonText,
                                            backgroundColor: 'transparent',
                                            border: `1px solid ${themeStyles.border}`,
                                            borderRadius: '6px',
                                            marginBottom: '8px',
                                            padding: '10px 12px',
                                            boxShadow: 'none',
                                            transition: 'border-color 0.2s ease'
                                        }}
                                        actions={[
                                            <Button
                                                type="text"
                                                icon={<EditOutlined />}
                                                style={{ 
                                                    color: themeStyles.text,
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    gap: '4px',
                                                }}
                                                onClick={() => handleEditClick(team)}
                                            >
                                                {t('edit')}
                                            </Button>,
                                            <Button
                                                type="text"
                                                danger
                                                icon={<DeleteOutlined />}
                                                style={{ 
                                                    display: 'flex',
                                                    alignItems: 'center',
                                                    gap: '4px',
                                                }}
                                                onClick={() => handleDeleteTeam(team.id)}
                                            >
                                                {t('delete')}
                                            </Button>,
                                        ]}
                                    >
                                        <List.Item.Meta
                                            avatar={
                                                <Avatar 
                                                    icon={<TeamOutlined />} 
                                                    style={{ 
                                                        backgroundColor: getRandomColor(team.name),
                                                        display: 'flex',
                                                        justifyContent: 'center',
                                                        alignItems: 'center',
                                                    }}
                                                    size="large"
                                                />
                                            }
                                            title={
                                                <Text strong style={{ color: themeStyles.commonText, fontSize: '16px' }}>
                                                    {team.name}
                                                </Text>
                                            }
                                            description={
                                                <Space>
                                                  <Badge >
                                                    <Text style={{ color: themeStyles.commonText }}>
                                                      <UserOutlined style={{ marginRight: '4px' }} />
                                                      {t('current_members') + ":"}
                                                    </Text>
                                                  </Badge>
                                                  <Text style={{ color: themeStyles.commonText }}>
                                                    {team.userCount}/{team.membersCount}
                                                  </Text>
                                                </Space>
                                            }
                                        />
                                    </List.Item>
                                </motion.div>
                            )}
                            locale={{ emptyText: t('no_teams') }}
                            pagination={{
                                pageSize: 5,
                                className: `custom-pagination ${theme === "dark" ? "custom-pagination-dark" : "custom-pagination-light"}`,
                            }}
                        />
                    </AnimatePresence>
                </Space>
            </WhiteCardWithLabel>

            <CreateTeamsModal
                visible={isCreateModalVisible}
                onCancel={() => setIsCreateModalVisible(false)}
                onSubmit={handleAddTeam}
                theme={theme}
                themeStyles={themeStyles}
                t={t}
            />

            <EditTeamModal
                visible={isEditModalVisible}
                onCancel={() => setIsEditModalVisible(false)}
                onSubmit={handleEditTeam}
                initialValues={editTeam}
                theme={theme}
                themeStyles={themeStyles}
                t={t}
            />
        </motion.div>
    );
};

export default TeamSettings;
