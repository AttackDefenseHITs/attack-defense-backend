export const generateColorFromName = (name) => {
    if (!name) return '#1890ff';

    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }

    const hue = Math.abs(hash % 360);
    return `hsl(${hue}, 70%, 60%)`;
};

export const getInitials = (name) => {
    if (!name) return '?';

    const parts = name.split(' ');
    if (parts.length === 1) return name.substring(0, 2).toUpperCase();

    return (parts[0][0] + parts[1][0]).toUpperCase();
};
