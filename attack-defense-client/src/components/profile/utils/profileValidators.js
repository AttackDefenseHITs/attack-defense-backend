export const validateProfileField = (field, value) => {
    if (field === 'login') {
      const loginRegex = /^[a-zA-Z0-9_]+$/;
      if (!loginRegex.test(value)) {
        return { isValid: false, message: 'Логин может содержать только английские буквы, цифры и нижние подчеркивания' };
      }
    }
    if (field === 'name') {
      if (!value.trim()) {
        return { isValid: false, message: 'Имя не может быть пустым' };
      }
    }
    return { isValid: true, message: '' };
  };
  