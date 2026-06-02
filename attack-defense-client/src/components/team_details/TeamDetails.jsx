import React, { useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Helmet } from "react-helmet";
import { useTranslation } from 'react-i18next';

import ThemeContext from '../../context/ThemeContext';

import useTeamDetails from './hooks/useTeamDetails';
import useFlagSubmit from './hooks/useFlagSubmit';

import LoadingScreen from './components/LoadingScreen';
import ErrorScreen from './components/ErrorScreen';
import TeamContentCard from './components/TeamContentCard';
import TeamRightColumn from './components/TeamRightColumn';

import styles from './styles/TeamDetails.module.css';

const TeamDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { theme, themeConfig } = useContext(ThemeContext);
    const themeStyles = themeConfig[theme];

    const { team, setTeam, loading, error } = useTeamDetails(id);
    const { submitting, handleFlagSubmit } = useFlagSubmit();

    if (loading) return <LoadingScreen themeStyles={themeStyles} t={t} />;
    if (error) return <ErrorScreen themeStyles={themeStyles} t={t} navigate={navigate} />;

    return (
        <div
            className="team-details-page"
            style={{ background: themeStyles.bannerGradient }}
        >
            <Helmet>
                <title>{team.name} - {t('team_details')}</title>
            </Helmet>

            <div className={styles.wrapper}>
                <TeamContentCard
                    team={team}
                    setTeam={setTeam}
                    themeStyles={themeStyles}
                    t={t}
                    navigate={navigate}
                />

                {team.isMyTeam && (
                    <TeamRightColumn
                        team={team}
                        themeStyles={themeStyles}
                        t={t}
                        submitting={submitting}
                        handleFlagSubmit={handleFlagSubmit}
                    />
                )}
            </div>
        </div>
    );
};

export default TeamDetails;