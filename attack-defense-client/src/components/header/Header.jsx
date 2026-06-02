import { useState, useContext, useEffect } from "react";
import { Outlet } from "react-router-dom";
import { Layout, Button } from "antd";
import { MenuOutlined, GlobalOutlined, BulbOutlined } from "@ant-design/icons";
import Logo from "./Logo";
import DesktopMenu from "./DesktopMenu";
import MobileMenu from "./MobileMenu";
import { useUser } from "../../context/UserContext";
import { useTranslation } from "react-i18next";
import ThemeContext from "../../context/ThemeContext";

const { Header: AntHeader, Content } = Layout;

const Header = () => {
  const [isDrawerVisible, setDrawerVisible] = useState(false);
  const { isAuthenticated, roles } = useUser();
  const { i18n, t } = useTranslation();
  const { theme, toggleTheme, themeConfig } = useContext(ThemeContext);

  const themeStyles = themeConfig[theme];

  const toggleDrawer = () => {
    setDrawerVisible(!isDrawerVisible);
  };

  const toggleLanguage = () => {
    const newLang = i18n.language === "ru" ? "en" : "ru";
    i18n.changeLanguage(newLang).then(() => {
      localStorage.setItem("language", newLang);
    });
  };

  useEffect(() => {
    const storedLang = localStorage.getItem("language");
    if (storedLang && storedLang !== i18n.language) {
      i18n.changeLanguage(storedLang);
    }
  }, [i18n]);

  const isAdmin = roles.includes("ADMIN");

  return (
    <Layout 
      style={{ 
        minHeight: "100vh", 
        backgroundColor: themeStyles.contentBackground
      }}
    >
      <AntHeader
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "0 20px",
          backgroundColor: themeStyles.background,
          borderBottom: `1px solid ${themeStyles.border}`,
          position: "fixed",
          top: 0,
          width: "100%",
          zIndex: 1000,
          height: "64px"
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <Logo isAuthenticated={isAuthenticated} />

          <Button
            type="text"
            onClick={toggleLanguage}
            style={{
              fontSize: "16px",
              display: "flex",
              alignItems: "center",
              gap: "6px",
              color: themeStyles.text,
            }}
          >
            <GlobalOutlined style={{ color: themeStyles.text }} />
            <span>{i18n.language.toUpperCase()}</span>
          </Button>

          {/*<Button*/}
          {/*  type="text"*/}
          {/*  onClick={toggleTheme}*/}
          {/*  style={{*/}
          {/*    fontSize: "16px",*/}
          {/*    display: "flex",*/}
          {/*    alignItems: "center",*/}
          {/*    gap: "6px",*/}
          {/*    color: themeStyles.highlight,*/}
          {/*  }}*/}
          {/*>*/}
          {/*  <BulbOutlined style={{ color: themeStyles.highlight }} />*/}
          {/*  <span>{theme === "dark" ? t('dark') : t('light')}</span>*/}
          {/*</Button>*/}
        </div>

        <DesktopMenu isAuthenticated={isAuthenticated} isAdmin={isAdmin} />

        <Button
          className="mobile-menu-button"
          type="text"
          icon={<MenuOutlined />}
          onClick={toggleDrawer}
        />
      </AntHeader>

      <MobileMenu
        isVisible={isDrawerVisible}
        toggleDrawer={toggleDrawer}
        isAuthenticated={isAuthenticated}
        isAdmin={isAdmin}
      />

      <Content 
        style={{ 
          backgroundColor: themeStyles.contentBackground,
          width: "100%"
        }}
      >
        <div 
          style={{ 
            width: "100%",
            backgroundColor: themeStyles.contentBackground,
          }}
        >
          <Outlet />
        </div>
      </Content>

      <style jsx>{`
        .mobile-menu-button {
          display: none;
        }
        @media (max-width: 1000px) {
          .mobile-menu-button {
            display: block;
          }
        }
      `}</style>
    </Layout>
  );
};

export default Header;
