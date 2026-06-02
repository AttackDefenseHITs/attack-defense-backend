import { useCallback, useEffect, useMemo, useState } from "react";
import { axiosGetAllTeams, axiosGetAllTeamMembers, axiosGetMyTeamStats } from "../../../api/requests/getTeamsRequest.js";
import { axiosGetTeamsDashboard } from "../../../api/requests/getTeamsDashboardRequest.js";
import { axiosGetCompetitionSettings } from "../../../api/requests/competitionRequests.js";
import { transformData } from "../../../utils/transformTeamDashboardData.js";

const MAX_EVENTS = 20;

export function useTeamsPageData({ registerEventHandler, unregisterEventHandler }) {
    const [teams, setTeams] = useState([]);
    const [members, setMembers] = useState([]);
    const [dashboardData, setDashboardData] = useState({ labels: [], datasets: [] });

    const [competitionSettings, setCompetitionSettings] = useState({
        name: "",
        startDate: "",
        endDate: "",
        status: "",
        rules: "",
        competitionMode: "",
        currentRound: 0,
        totalRounds: 60,
        servicesCount: 0,
        flagsSubmitted: 0,
    });

    const [events, setEvents] = useState([]);
    const [myTeam, setMyTeam] = useState(null);

    const [loading, setLoading] = useState({
        teams: true,
        members: false,
        dashboard: true,
        settings: true,
        events: true,
        myTeam: true,
    });

    const [membersFetched, setMembersFetched] = useState(false);

    const normalizeMyTeam = useCallback((d) => {
        if (!d || !d.id) return null;
        return {
            id: d.id,
            name: d.name,
            online: !!d.online,
            hintsBought: d.hintsBought ?? 0,
            stack: d.stack ?? "—",
            services: Array.isArray(d.services)
                ? d.services.map((s) => ({
                    name: s.name,
                    online: !!s.online,
                    uptimePct: s.uptimePct ?? 0,
                }))
                : [],
        };
    }, []);

    const fetchTeams = useCallback(async () => {
        setLoading((s) => ({ ...s, teams: true }));
        try {
            const res = await axiosGetAllTeams();
            const list = Array.isArray(res?.data) ? res.data : [];

            const mapped = list.map((team) => ({
                key: team.id,
                name: team.name,
                place: team.place,
                points: team.points,
                ip: team.ipAddress,
                members: `${team.userCount ?? 0}/${team.membersCount ?? 0}`,
                isMyTeam: !!team.isMyTeam,
            }));

            setTeams(mapped);
        } catch (e) {
            setTeams([]);
        } finally {
            setLoading((s) => ({ ...s, teams: false }));
        }
    }, []);

    const fetchMyTeam = useCallback(async () => {
        setLoading((s) => ({ ...s, myTeam: true }));
        try {
            const res = await axiosGetMyTeamStats();
            setMyTeam(normalizeMyTeam(res?.data));
        } catch (e) {
            setMyTeam(null);
        } finally {
            setLoading((s) => ({ ...s, myTeam: false }));
        }
    }, [normalizeMyTeam]);

    const fetchMembers = useCallback(async () => {
        if (membersFetched) return;
        setLoading((s) => ({ ...s, members: true }));
        try {
            const res = await axiosGetAllTeamMembers();
            const list = Array.isArray(res?.data) ? res.data : [];

            setMembers(
                list.map((m) => ({
                    id: m.userId,
                    name: m.name,
                    login: m.login,
                    points: m.points,
                    isCurrentUser: m.isCurrentUser,
                }))
            );

            setMembersFetched(true);
        } catch (e) {
            setMembers([]);
        } finally {
            setLoading((s) => ({ ...s, members: false }));
        }
    }, [membersFetched]);

    const fetchDashboard = useCallback(async () => {
        setLoading((s) => ({ ...s, dashboard: true }));
        try {
            const data = await axiosGetTeamsDashboard(true);
            setDashboardData(transformData(Array.isArray(data) ? data : []));
        } catch (e) {
            setDashboardData(transformData([]));
        } finally {
            setLoading((s) => ({ ...s, dashboard: false }));
        }
    }, []);

    const fetchSettings = useCallback(async () => {
        setLoading((s) => ({ ...s, settings: true }));
        try {
            const res = await axiosGetCompetitionSettings();
            const d = res?.data;
            if (!d) return;

            setCompetitionSettings((prev) => ({
                ...prev,
                name: d.name ?? prev.name,
                startDate: d.startDate ?? prev.startDate,
                endDate: d.endDate ?? prev.endDate,
                status: d.status ?? prev.status,
                rules: d.rules ?? prev.rules,
                competitionMode: d.competitionMode ?? prev.competitionMode,
                currentRound: d.currentRound ?? prev.currentRound,
                totalRounds: d.totalRounds ?? prev.totalRounds,
                servicesCount: d.servicesCount ?? prev.servicesCount,
                flagsSubmitted: d.flagsSubmitted ?? prev.flagsSubmitted,
            }));
        } catch (e) {
            // noop
        } finally {
            setLoading((s) => ({ ...s, settings: false }));
        }
    }, []);

    const fetchEvents = useCallback(async () => {
        setLoading((s) => ({ ...s, events: true }));
        try {
            // TODO: заменить на axiosGetEvents()
            setEvents([]);
        } finally {
            setLoading((s) => ({ ...s, events: false }));
        }
    }, []);

    // WS handlers
    const onSettingsUpdate = useCallback(
        (msg) => {
            if (msg?.data) fetchSettings();
        },
        [fetchSettings]
    );

    const onEventFeed = useCallback((msg) => {
        const payload = msg?.data;
        if (!payload) return;
        setEvents((prev) => [payload, ...prev].slice(0, MAX_EVENTS));
    }, []);

    const onMyTeamUpdate = useCallback(
        (msg) => {
            const payload = msg?.data;
            if (!payload) return;
            setMyTeam(normalizeMyTeam(payload));
        },
        [normalizeMyTeam]
    );

    useEffect(() => {
        fetchTeams();
        fetchDashboard();
        fetchSettings();
        fetchEvents();
        fetchMyTeam();

        registerEventHandler("competition-settings-update", onSettingsUpdate);
        registerEventHandler("event-feed", onEventFeed);
        registerEventHandler("my-team-update", onMyTeamUpdate);

        return () => {
            unregisterEventHandler("competition-settings-update", onSettingsUpdate);
            unregisterEventHandler("event-feed", onEventFeed);
            unregisterEventHandler("my-team-update", onMyTeamUpdate);
        };
    }, [
        fetchTeams,
        fetchDashboard,
        fetchSettings,
        fetchEvents,
        fetchMyTeam,
        registerEventHandler,
        unregisterEventHandler,
        onSettingsUpdate,
        onEventFeed,
        onMyTeamUpdate,
    ]);

    const metrics = useMemo(
        () => ({
            teamsCount: teams.length,
            servicesCount: competitionSettings.servicesCount || 0,
            flagsSubmitted: competitionSettings.flagsSubmitted || 0,
            currentRound: competitionSettings.currentRound ?? 0,
            totalRounds: competitionSettings.totalRounds ?? 60,
        }),
        [teams.length, competitionSettings]
    );

    return {
        teams,
        members,
        dashboardData,
        competitionSettings,
        events,
        myTeam,
        metrics,
        loading,
        fetchMembers,
        fetchMyTeam,
    };
}