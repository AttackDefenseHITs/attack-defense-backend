import React, { useContext } from 'react';
import { Helmet } from "react-helmet";
import { Button, Divider } from 'antd';
import { HomeOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import ThemeContext from "../../context/ThemeContext";
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

import { useProfile } from './hooks/useProfile';
import { getRoleInfo } from './utils/roleMapper';

import { ProfileHeader } from './components/ProfileHeader';
import { ProfileInfoBlock } from './components/ProfileInfoBlock';
import { ProfileAccessBlock } from './components/ProfileAccessBlock';
import { ProfileSkeleton } from './components/ProfileSkeleton';
import { ProfileContainer } from './components/ProfileContainer';

const Profile = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];

    const {
        profile,
        loading,
        editingField,
        updatedValue,
        setUpdatedValue,
        startEditing,
        saveField,
        cancelEditing
    } = useProfile(t);

    if (loading) return <ProfileSkeleton themeStyles={themeStyles} />;
    if (!profile) return <div>{t('profile_not_found')}</div>;

    const { label: roleLabel, color: roleColor } = getRoleInfo(profile.role, t);

    return (
        <>
            <Helmet><title>{t('profile')} - AD</title></Helmet>

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
                style={{ padding: '20px', maxWidth: '700px', margin: '0 auto' }}
            >
                <ProfileContainer themeStyles={themeStyles}>
                    <ProfileHeader
                        profile={profile}
                        roleColor={roleColor}
                        roleLabel={roleLabel}
                        themeStyles={themeStyles}
                    />

                    <Divider />

                    <ProfileInfoBlock
                        profile={profile}
                        themeStyles={themeStyles}
                        editingField={editingField}
                        updatedValue={updatedValue}
                        setUpdatedValue={setUpdatedValue}
                        startEditing={startEditing}
                        saveField={saveField}
                        cancelEditing={cancelEditing}
                        t={t}
                    />

                    <Divider />

                    <ProfileAccessBlock
                        profile={profile}
                        roleColor={roleColor}
                        roleLabel={roleLabel}
                        themeStyles={themeStyles}
                        t={t}
                    />

                    <Divider />

                    <div style={{ textAlign: 'center' }}>
                        <Button type="primary" icon={<HomeOutlined />} onClick={() => navigate('/')}>
                            {t('go_to_teams')}
                        </Button>
                    </div>
                </ProfileContainer>
            </motion.div>
        </>
    );
};

export default Profile;
