import React, { useEffect, useState, useContext } from 'react';
import { Form, Input, InputNumber, Modal, List, Button, message, Typography, Space, Divider } from 'antd';
import { DeleteOutlined, UserOutlined, TeamOutlined, TrophyOutlined } from '@ant-design/icons';
import { motion, AnimatePresence } from 'framer-motion';
import { axiosRemoveMemberFromTeam } from '../../../api/requests/adminTeamsRequests';
import { axiosGetTeamById } from '../../../api/requests/getTeamByIdRequest';
import ThemeContext from "../../../context/ThemeContext";
import { getFormItemStyle, getInputStyle, getLabelStyle } from '../../styles/modalStyles';

const { Text } = Typography;

const EditTeamModal = ({ visible, onCancel, onSubmit, initialValues, t }) => {
    const [form] = Form.useForm();
    const [teamDetails, setTeamDetails] = useState(null);
    const [loading, setLoading] = useState(false);
    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];

    const formItemStyle = getFormItemStyle(theme);
    const inputStyle = getInputStyle(theme, themeStyles);
    const labelStyle = getLabelStyle(themeStyles);

    useEffect(() => {
        if (initialValues && initialValues.id) {
            fetchTeamDetails(initialValues.id);
            form.setFieldsValue({
                name: initialValues.name,
                maxMembers: initialValues.membersCount,
            });
        }
    }, [initialValues, form]);

    const fetchTeamDetails = async (teamId) => {
        try {
            setLoading(true);
            const response = await axiosGetTeamById(teamId);
            setTeamDetails(response.data);
        } catch (error) {
            message.error(t('team_load_error'));
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (values) => {
        if (initialValues) {
            await onSubmit({ id: initialValues.id, ...values });
        }
    };

    const handleRemoveMember = async (teamId, memberId) => {
        try {
            await axiosRemoveMemberFromTeam(teamId, memberId);
            message.success(t('member_removed'));
            setTeamDetails((prevDetails) => ({
                ...prevDetails,
                memberList: prevDetails.memberList.filter((member) => member.id !== memberId),
            }));
        } catch (error) {
            message.error(t('remove_member_error'));
        }
    };

    if (!initialValues || !teamDetails) return null;

    return (
        <Modal
            title={t('edit_team')}
            open={visible}
            onCancel={onCancel}
            onOk={() => form.submit()}
            width={520}
            okButtonProps={{
              style: {
                backgroundColor: themeStyles.primaryButton,
                borderColor: themeStyles.primaryButton,
                color: themeStyles.primaryButtonText,
              }
            }}
            styles={{
              content: { backgroundColor: themeStyles.window },
              header: {
                backgroundColor: themeStyles.window,
                borderBottom: `1px solid ${themeStyles.border}`,
                padding: '16px 24px',
              },
              body: { padding: 24 },
              footer: {
                backgroundColor: themeStyles.window,
                borderTop: `1px solid ${themeStyles.border}`,
                padding: '16px 24px',
              },
            }}
        >
            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.3 }}
            >
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>       
                    <Form
                        form={form}
                        layout="vertical"
                        onFinish={handleSubmit}
                        style={{ color: themeStyles.commonText }}
                        size="small"
                    >
                       <Form.Item
                            style={formItemStyle}
                            label={
                                <Space>
                                    <TeamOutlined />
                                    <Text style={labelStyle}>{t('team_name')}</Text>
                                </Space>
                            }
                            name="name"
                            rules={[{ required: true, message: t('enter_team_name') }]}
                        >
                            <Input 
                                placeholder={t('team_name_placeholder')} 
                                style={inputStyle}
                                prefix={<TeamOutlined style={{ color: themeStyles.icon || '#1677ff', marginRight: '8px' }} />}
                            />
                        </Form.Item>
                        
                        <Form.Item
                            style={formItemStyle}
                            label={
                                <Space>
                                    <UserOutlined />
                                    <Text style={labelStyle}>{t('max_members')}</Text>
                                </Space>
                            }
                            name="maxMembers"
                            rules={[{ required: true, message: t('enter_max_members') }]}
                        >
                            <InputNumber 
                                min={2} 
                                placeholder={t('max_members_placeholder')} 
                                style={{ width: '100%', ...inputStyle }} 
                                controls={false}
                            />
                        </Form.Item>

                    </Form>

                    <Divider style={{ margin: '4px 0 4px', borderColor: themeStyles.border }} />
                    
                    <Text strong style={{ color: themeStyles.commonText, fontSize: '16px', marginBottom: '8px' }}>
                        {t('member_list')}
                    </Text>

                    <AnimatePresence>
                        <div
                            style={{
                                maxHeight: 3 * 68 + 2 * 8, // 3 элемента + 2 промежутка (marginBottom: 8px)
                                overflowY: 'auto',
                                paddingRight: 4, // чтобы скролл не прижимал контент (опционально)
                            }}
                        >
                            <List
                                bordered={false}
                                dataSource={teamDetails.memberList}
                                style={{
                                    color: themeStyles.commonText,
                                    borderColor: themeStyles.border,
                                }}
                                renderItem={(member) => (
                                    <motion.div
                                        key={member.id}
                                        initial={{ opacity: 0, y: 10 }}
                                        animate={{ opacity: 1, y: 0 }}
                                        exit={{ opacity: 0, y: -10 }}
                                        transition={{ duration: 0.3 }}
                                    >
                                        <List.Item
                                            style={{
                                                color: themeStyles.commonText,
                                                backgroundColor: themeStyles.contentBackground,
                                                borderColor: themeStyles.border,
                                                borderRadius: '8px',
                                                marginBottom: '8px',
                                                padding: '12px 16px',
                                            }}
                                            actions={[
                                                <Button
                                                    type="text"
                                                    danger
                                                    icon={<DeleteOutlined />}
                                                    style={{
                                                        display: 'flex',
                                                        alignItems: 'center',
                                                        gap: '4px',
                                                    }}
                                                    onClick={() => handleRemoveMember(teamDetails.id, member.id)}
                                                >
                                                    {t('delete')}
                                                </Button>,
                                            ]}
                                        >
                                            <List.Item.Meta
                                                avatar={
                                                    <UserOutlined
                                                        style={{
                                                            color: themeStyles.icon || '#1677ff',
                                                            fontSize: '20px',
                                                        }}
                                                    />
                                                }
                                                title={
                                                    <Text style={{ color: themeStyles.commonText, fontWeight: 500 }}>
                                                        {member.name || member.login}
                                                    </Text>
                                                }
                                                description={
                                                    <Space>
                                                        <Text style={{ color: themeStyles.secondaryText }}>
                                                            {t('role')}: {member.role}
                                                        </Text>
                                                        <Text style={{ color: themeStyles.secondaryText }}>
                                                            <TrophyOutlined style={{ marginRight: '4px', color: '#faad14' }} />
                                                            {t('score')}: {member.points}
                                                        </Text>
                                                    </Space>
                                                }
                                            />
                                        </List.Item>
                                    </motion.div>
                                )}
                                locale={{ emptyText: t('no_members') }}
                            />
                        </div>
                    </AnimatePresence>
                </Space>
            </motion.div>
        </Modal>
    );
};

export default EditTeamModal;
