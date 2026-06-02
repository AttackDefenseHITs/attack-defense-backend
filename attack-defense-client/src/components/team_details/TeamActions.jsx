import React from 'react';
import { Space, Button, message } from 'antd';
import { useTranslation } from 'react-i18next';
import { axiosJoinTeam } from '../../api/requests/joinTeamRequest';
import { axiosLeaveTeam } from '../../api/requests/leaveTeamRequest';
import { axiosGetTeamById } from '../../api/requests/getTeamByIdRequest';
import { ROUTES } from '../../constants/routes';

const TeamActions = ({
  team,
  setTeam,
  navigate,
  primaryButtonColor = '#1890ff',
  dangerButtonColor = '#ff4d4f',
  defaultButtonColor = '#f0f0f0',
  textColor = '#000',
}) => {
  const { t } = useTranslation();

  const handleJoinTeam = async () => {
    try {
      await axiosJoinTeam(team.id);
      const updatedTeam = await axiosGetTeamById(team.id);
      setTeam(updatedTeam.data);
      message.success(t("joined_team"));
    } catch (err) {
      message.error(t("join_team_error"));
    }
  };

  const handleLeaveTeam = async () => {
    try {
      await axiosLeaveTeam(team.id);
      const updatedTeam = await axiosGetTeamById(team.id);
      setTeam(updatedTeam.data);
      message.success(t("left_team"));
    } catch (err) {
      message.error(t("leave_team_error"));
    }
  };

  return (
    <Space
      style={{
        marginTop: '20px',
        display: 'flex',
        justifyContent: 'center',
        gap: '10px',
        flexWrap: 'wrap',
      }}
    >
      {team.canJoin && !team.isMyTeam && (
        <Button
          type="primary"
          onClick={handleJoinTeam}
          style={{ backgroundColor: primaryButtonColor, borderColor: primaryButtonColor, color: '#ffffff' }}
        >
          {t("join")}
        </Button>
      )}
      {team.canLeave && (
        <Button
          danger
          onClick={handleLeaveTeam}
          style={{ backgroundColor: dangerButtonColor, borderColor: dangerButtonColor, color: '#ffffff' }}
        >
          {t("leave")}
        </Button>
      )}
      <Button
        type="default"
        onClick={() => navigate(ROUTES.ROOT)}
        style={{ backgroundColor: defaultButtonColor, color: textColor }}
      >
        {t("back")}
      </Button>
    </Space>
  );
};

export default TeamActions;