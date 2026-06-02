import { useState, useEffect } from 'react';
import { message } from 'antd';
import { axiosGetUserProfile } from '../../../api/requests/getUserProfileRequest';
import { axiosUpdateUserProfile } from '../../../api/requests/putUserProfileRequest';
import { validateProfileField } from '../utils/profileValidators.js';

export const useProfile = (t) => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [editingField, setEditingField] = useState(null);
    const [updatedValue, setUpdatedValue] = useState('');

    useEffect(() => {
        const fetchProfile = async () => {
            setLoading(true);
            try {
                const response = await axiosGetUserProfile();
                setProfile(response.data);
            } catch {
                message.error(t('error_loading_profile'));
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [t]);

    const startEditing = (field, value) => {
        setEditingField(field);
        setUpdatedValue(value);
    };

    const cancelEditing = () => {
        setEditingField(null);
        setUpdatedValue('');
    };

    const saveField = async (field) => {
        const validation = validateProfileField(field, updatedValue);

        if (!validation.isValid) {
            message.error(validation.message);
            return;
        }

        try {
            const updated = { ...profile, [field]: updatedValue };
            await axiosUpdateUserProfile(updated);
            setProfile(updated);
        } catch {
            message.error(t('error_save'));
        } finally {
            cancelEditing();
        }
    };

    return {
        profile,
        loading,
        editingField,
        updatedValue,
        setUpdatedValue,
        startEditing,
        saveField,
        cancelEditing
    };
};
