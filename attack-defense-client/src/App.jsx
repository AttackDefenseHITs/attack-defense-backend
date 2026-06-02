import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ConfigProvider } from 'antd';
import ruRU from 'antd/locale/ru_RU.js';
import { UserProvider } from './context/UserContext';
import { EventWebSocketProvider } from './context/EventWebSocketContext';
import { CheckerWebSocketProvider } from './context/CheckerWebSocketContext';
import Instances from './components/main/Teams';
import NotFound from './components/error/NotFound';

import { ROUTES } from './constants/routes';

import './App.css';
import Header from './components/header/Header';
import Login from './components/Login';
import Registration from './components/Registration';
import TeamDetails from './components/team_details/TeamDetails';
import Profile from './components/profile/Profile';
import AdminPanel from './components/admin/AdminPanel';
import Checker from './components/checker/Checker';
import LearningMaterials from './components/learning_materials/LearningMaterials';

const router = createBrowserRouter([
  {
    path: ROUTES.ROOT,
    element: <Header />,
    children: [
      {
        path: ROUTES.ROOT,
        element: <Instances />,
      },
      {
        path: ROUTES.LOGIN,
        element: <Login />,
      },
      {
        path: ROUTES.REGISTRATION,
        element: <Registration />,
      },
      {
        path: ROUTES.TEAM_DETAILS,
        element: <TeamDetails />,
      },
      {
        path: ROUTES.PROFILE,
        element: <Profile />,
      },
      {
        path: ROUTES.LEARNING_MATERIALS,
        element: <LearningMaterials />,
      },
      {
        path: ROUTES.ADMIN,
        element: <AdminPanel />,
      },
      {
        path: ROUTES.CHECKERS,
        element: <CheckerWebSocketProvider active={true}> <Checker /> </CheckerWebSocketProvider>
      },
      {
        path: '*',
        element: <NotFound />,
      }
    ],
  },
]);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
    },
  },
});

export const App = () => (
  <UserProvider>
    <ConfigProvider locale={ruRU}>
      <QueryClientProvider client={queryClient}>
        <EventWebSocketProvider>
          <RouterProvider router={router} />
        </EventWebSocketProvider>
      </QueryClientProvider>
    </ConfigProvider>
  </UserProvider>
);

export default App;
