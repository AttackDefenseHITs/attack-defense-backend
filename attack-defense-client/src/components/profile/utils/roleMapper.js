export const getRoleInfo = (role, t) => {
    if (role === 'USER') {
      return { label: t('member'), color: 'blue' };
    } else if (role === 'ADMIN') {
      return { label: t('admin'), color: 'gold' };
    }
    return { label: t('unknown'), color: 'gray' };
  };
  