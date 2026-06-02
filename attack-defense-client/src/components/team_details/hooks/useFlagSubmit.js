import { useState } from 'react';
import { axiosSendFlagRequest } from '../../../api/requests/sendFlagRequest.js';

const useFlagSubmit = () => {
    const [submitting, setSubmitting] = useState(false);

    const handleFlagSubmit = async (flag) => {
        if (!flag.trim()) return;

        setSubmitting(true);
        try {
            await axiosSendFlagRequest(flag);
        } finally {
            setSubmitting(false);
        }
    };

    return { submitting, handleFlagSubmit };
};

export default useFlagSubmit;
