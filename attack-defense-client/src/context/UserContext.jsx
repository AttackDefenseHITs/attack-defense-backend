import { createContext, useContext, useState, useEffect } from 'react';
import { axiosGetRole } from '../api/requests/getUserRole';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [roles, setRoles] = useState([]);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const fetchUserRole = async () => {
        const response = await axiosGetRole();
        const userRole = response?.data?.role;
        if (userRole) {
            setIsAuthenticated(true);
            setRoles([userRole]);
        }
    };

    useEffect(() => {
        fetchUserRole();
    }, []);

    const login = (userRoles) => {
        setIsAuthenticated(true);
        setRoles(userRoles);
    };

    const logout = () => {
        setIsAuthenticated(false);
        setRoles([]);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
    };

    const addRole = (role) => {
        setRoles((prevRoles) => {
            if (!prevRoles.includes(role)) {
                return [...prevRoles, role];
            }
            return prevRoles;
        });
    };

    const removeRole = (role) => {
        setRoles((prevRoles) => prevRoles.filter(r => r !== role));
    };

    return (
        <UserContext.Provider value={{ isAuthenticated, roles, login, logout, addRole, removeRole }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => useContext(UserContext);
