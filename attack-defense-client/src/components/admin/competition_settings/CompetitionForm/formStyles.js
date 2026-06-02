export const buildInputStyle = ({ theme, textColor, backgroundColor }) => ({
    color: textColor,
    backgroundColor,
    borderRadius: 6,
    border: `1px solid ${
        theme === "dark" ? "rgba(255,255,255,0.15)" : "rgba(0,0,0,0.15)"
    }`,
    height: 32,
});


export const buildNumberStyle = (base) => ({
    ...base,
    width: "100%",
});


export const buttonStyleBase = {
    borderRadius: 4,
    height: 32,
    padding: "0 12px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
};