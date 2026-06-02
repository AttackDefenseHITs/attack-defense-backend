import React, { useState, useEffect, useContext, useCallback, useMemo } from 'react';
import { Menu, Typography, Space, Card, Button } from 'antd';
import {
    SettingOutlined,
    TeamOutlined,
    CloudServerOutlined,
    AppstoreOutlined,
    DashboardOutlined,
    RocketOutlined,
    FlagOutlined,
    UserOutlined,
    RiseOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined
} from '@ant-design/icons';
import { CSSTransition, SwitchTransition } from 'react-transition-group';

import CompetitionSettings from './competition_settings/CompetitionSettings';
import VirtualMachines from './vm/VirtualMachines';
import ServicesManagement from './services/ServicesManagement';
import TeamSettings from './teams/TeamSettings';
import CompetitionStatus from './status/CompetitionStatus';
import FlagsManagement from './flags/FlagsManagement';
import UsersManagement from './users/UsersManagement';
import FlagSubmissionsTable from './submissions/FlagSubmissionsTable';
import DeploymentTable from './deploy/DeploymentTable.jsx';

import { useUser } from '../../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { axiosGetAllVms } from '../../api/requests/vmRequests';
import { axiosGetAllTeams } from '../../api/requests/getTeamsRequest';
import { axiosGetAllServices } from '../../api/requests/servicesRequests.js';
import { DeployWebSocketProvider } from '../../context/DeployWebSocketContext';
import { useTranslation } from "react-i18next";
import ThemeContext from '../../context/ThemeContext';

import '../styles/AdminPanel.css';

const { Title } = Typography;

const AdminPanel = () => {
    const { isAuthenticated, roles } = useUser();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];

    const isAdmin = roles.includes('ADMIN');

    const [activeSection, setActiveSection] = useState('competitionSettings');
    const [collapsed, setCollapsed] = useState(false);

    const [dataCache, setDataCache] = useState({
        vms: null,
        teams: null,
        competitionSettings: null,
        services: null,
    });

    const loadVirtualMachinesData = useCallback(async () => {
        if (dataCache.vms && dataCache.teams) return;

        try {
            const [vmsResponse, teamsResponse] = await Promise.all([
                axiosGetAllVms(),
                axiosGetAllTeams(),
            ]);

            setDataCache((prev) => ({
                ...prev,
                vms: vmsResponse.data,
                teams: teamsResponse.data,
            }));
        } catch (error) {
            console.error('Ошибка загрузки данных виртуальных машин:', error);
        }
    }, [dataCache.vms, dataCache.teams]);

    const loadServicesData = useCallback(async () => {
        if (dataCache.services) return;

        try {
            const response = await axiosGetAllServices();
            setDataCache((prev) => ({
                ...prev,
                services: response?.data || [],
            }));
        } catch (error) {
            console.error('Ошибка загрузки сервисов:', error);
        }
    }, [dataCache.services]);

    useEffect(() => {
        if (!isAuthenticated || !isAdmin) {
            navigate('/');
        }
    }, [isAuthenticated, isAdmin, navigate]);

    useEffect(() => {
        if (activeSection === 'virtualMachines') loadVirtualMachinesData();
        if (activeSection === 'servicesManagement') loadServicesData();
    }, [activeSection, loadVirtualMachinesData, loadServicesData]);

    const menuItems = useMemo(() => ([
        { key: 'competitionSettings', icon: <SettingOutlined />, label: t('competition_settings') },
        { key: 'competitionStatus', icon: <DashboardOutlined />, label: t('competition_status') },
        { key: 'teamSettings', icon: <TeamOutlined />, label: t('teams') },
        { key: 'virtualMachines', icon: <CloudServerOutlined />, label: t('vm') },
        { key: 'servicesManagement', icon: <AppstoreOutlined />, label: t('services') },
        { key: 'deployment', icon: <RocketOutlined />, label: t('deploy') },
        { key: 'flagsManagement', icon: <FlagOutlined />, label: t('flags') },
        { key: 'usersManagement', icon: <UserOutlined />, label: t('users') },
        { key: 'submissions', icon: <RiseOutlined />, label: t('submissions') },
    ].map((it) => ({
        ...it,
        style: { color: themeStyles.commonText },
    }))), [t, themeStyles.commonText]);


    const renderSection = () => {
        switch (activeSection) {
            case 'competitionSettings':
                return (
                    <CompetitionSettings
                        initialData={dataCache.competitionSettings}
                        setCompetitionSettings={(newData) =>
                            setDataCache((prev) => ({ ...prev, competitionSettings: newData }))
                        }
                    />
                );

            case 'teamSettings':
                return <TeamSettings />;

            case 'virtualMachines':
                return (
                    <VirtualMachines
                        vms={dataCache.vms || []}
                        setVms={(newVms) => setDataCache((prev) => ({ ...prev, vms: newVms }))}
                        teams={dataCache.teams || []}
                    />
                );

            case 'servicesManagement':
                return (
                    <ServicesManagement
                        services={dataCache.services || []}
                        setServices={(newServices) =>
                            setDataCache((prev) => ({ ...prev, services: newServices }))
                        }
                    />
                );

            case 'flagsManagement':
                return <FlagsManagement />;

            case 'usersManagement':
                return <UsersManagement />;

            case 'competitionStatus':
                return <CompetitionStatus />;

            case 'deployment':
                return (
                    <DeployWebSocketProvider active={true}>
                        <DeploymentTable />
                    </DeployWebSocketProvider>
                );

            case 'submissions':
                return <FlagSubmissionsTable />;

            default:
                return null;
        }
    };

    if (!isAuthenticated || !isAdmin) return null;

    const sidebarWidth = collapsed ? 80 : 280;

    return (
        <div
            className="admin-panel-page fade-in"
            style={{
                // было 40px — стало компактнее и адаптивнее
                padding: 'clamp(12px, 2vw, 24px)',
                background: themeStyles.bannerGradient,
            }}
        >
            <Space direction="vertical" style={{ width: '100%', marginBottom: 16 }}>
                <div className="slide-up" style={{ display: 'flex', justifyContent: 'center', marginTop: 4 }}>
                    <div
                        style={{
                            background: themeStyles.window,
                            padding: '14px 18px',
                            borderRadius: 12,
                            display: 'flex',
                            alignItems: 'center',
                            gap: 12,
                            boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
                            border: `1px solid ${themeStyles.border}`,
                        }}
                    >
                        <SettingOutlined style={{ fontSize: 26, color: themeStyles.primary }} />
                        <Title
                            level={2}
                            style={{
                                margin: 0,
                                color: themeStyles.commonText,
                                fontWeight: 500,
                                letterSpacing: '0.3px',
                            }}
                        >
                            {t('admin_panel')}
                        </Title>
                    </div>
                </div>
            </Space>

            <div
                style={{
                    // было 1400 — можно расширить или убрать вовсе
                    maxWidth: 1700,
                    margin: '0 auto',
                }}
            >
                <div style={{ display: 'flex', gap: 16, alignItems: 'flex-start' }}>
                    <Card
                        className="custom-card slide-up"
                        style={{
                            width: sidebarWidth,
                            minWidth: sidebarWidth,
                            borderRadius: 16,
                            backgroundColor: themeStyles.cardBackground,
                            border: `1px solid ${themeStyles.cardBorder}`,
                            boxShadow: themeStyles.cardShadow,
                            overflow: 'hidden',
                        }}
                        bodyStyle={{ padding: 0 }}
                    >
                        <div
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: collapsed ? 'center' : 'space-between',
                                padding: '12px 12px',
                                borderBottom: `1px solid ${themeStyles.border}`,
                                background: themeStyles.window,
                            }}
                        >
                            {!collapsed && (
                                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ color: themeStyles.commonText, fontWeight: 600, paddingLeft: 16 }}>
                    Меню
                  </span>
                                </div>
                            )}

                            <Button
                                type="text"
                                onClick={() => setCollapsed((v) => !v)}
                                icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                                style={{ color: themeStyles.commonText }}
                            />
                        </div>

                        <Menu
                            mode="inline"
                            inlineCollapsed={collapsed}
                            selectedKeys={[activeSection]}
                            onClick={(e) => setActiveSection(e.key)}
                            items={menuItems}
                            style={{
                                background: 'transparent',
                                borderRight: 'none',
                                padding: 8,
                            }}
                        />
                    </Card>

                    <Card
                        className="custom-card slide-up"
                        style={{
                            flex: 1,
                            minWidth: 0,
                            borderRadius: 16,
                            backgroundColor: themeStyles.cardBackground,
                            border: `1px solid ${themeStyles.cardBorder}`,
                            boxShadow: themeStyles.cardShadow,
                        }}
                    >
                        <div style={{ position: 'relative' }}>
                            <SwitchTransition>
                                <CSSTransition key={activeSection} timeout={300} classNames="fade" unmountOnExit>
                                    <div>{renderSection()}</div>
                                </CSSTransition>
                            </SwitchTransition>
                        </div>
                    </Card>
                </div>
            </div>
        </div>
    );
};

export default AdminPanel;
