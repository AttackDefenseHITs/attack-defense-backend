export const getFormItemStyle = (theme) => ({
  marginBottom: 12,
  borderRadius: 6,
  padding: '10px 12px',
  background: theme === 'dark' ? 'rgba(255, 255, 255, 0.04)' : 'rgba(0, 0, 0, 0.02)',
  border: `1px solid ${theme === 'dark' ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'}`,
  transition: 'all 0.3s ease',
});

export const getInputStyle = (theme, themeStyles) => ({
  color: themeStyles.inputText,
  backgroundColor: themeStyles.inputBackground,
  borderRadius: 4,
  height: '32px',
  border: `1px solid ${theme === 'dark' ? 'rgba(255, 255, 255, 0.15)' : 'rgba(0, 0, 0, 0.1)'}`,
  transition: 'all 0.3s ease',
});

export const getLabelStyle = (themeStyles) => ({
  color: themeStyles.commonText,
  fontSize: '13px',
  marginBottom: '4px',
  fontWeight: 500,
});
