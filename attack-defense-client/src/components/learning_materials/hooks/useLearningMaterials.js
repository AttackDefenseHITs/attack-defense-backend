import { useEffect, useMemo, useState } from "react";
import { message } from "antd";
import { initialLearningPages } from "../data/learningMaterialsData";

const createId = () => `${Date.now()}_${Math.random().toString(16).slice(2)}`;

export const useLearningMaterials = ({ isAdmin, t }) => {
    const [pages, setPages] = useState(() => initialLearningPages.map((p) => ({ ...p })));
    const [selectedPageId, setSelectedPageId] = useState(() => initialLearningPages[0]?.id || null);
    const [isEditMode, setIsEditMode] = useState(false);

    const selectedPage = useMemo(
        () => pages.find((p) => p.id === selectedPageId) || null,
        [pages, selectedPageId]
    );

    const visiblePages = useMemo(() => {
        return isAdmin ? pages : pages.filter((p) => p.visibleToUsers);
    }, [pages, isAdmin]);

    useEffect(() => {
        setIsEditMode(false);
    }, [selectedPageId]);

    useEffect(() => {
        if (isAdmin) return;

        const visible = pages.filter((p) => p.visibleToUsers);
        const currentVisible = visible.some((p) => p.id === selectedPageId);

        if (!currentVisible) {
            setSelectedPageId(visible[0]?.id || null);
        }
    }, [pages, selectedPageId, isAdmin]);

    const updateSelectedPage = (patch) => {
        if (!selectedPageId) return;
        setPages((prev) => prev.map((p) => (p.id === selectedPageId ? { ...p, ...patch } : p)));
    };

    const handleAddPage = () => {
        const id = createId();

        setPages((prev) => [
            ...prev,
            {
                id,
                title: "Без названия",
                content: "",
                visibleToUsers: false,
            },
        ]);

        setSelectedPageId(id);
        setIsEditMode(true);
        message.success("Страница создана");
    };

    const handleDeleteSelectedPage = () => {
        if (!selectedPage) return;

        const currentIndex = pages.findIndex((p) => p.id === selectedPage.id);
        const nextPages = pages.filter((p) => p.id !== selectedPage.id);

        setPages(nextPages);

        if (nextPages.length === 0) {
            setSelectedPageId(null);
            setIsEditMode(false);
            message.success("Страница удалена");
            return;
        }

        const nextPage =
            nextPages[currentIndex] ||
            nextPages[currentIndex - 1] ||
            nextPages[0] ||
            null;

        setSelectedPageId(nextPage?.id || null);
        setIsEditMode(false);
        message.success("Страница удалена");
    };

    const toggleSelectedPageVisibility = () => {
        if (!selectedPage) return;

        updateSelectedPage({
            visibleToUsers: !selectedPage.visibleToUsers,
        });
    };

    return {
        pages,
        setPages,
        selectedPage,
        selectedPageId,
        setSelectedPageId,
        isEditMode,
        setIsEditMode,
        visiblePages,
        updateSelectedPage,
        handleAddPage,
        handleDeleteSelectedPage,
        toggleSelectedPageVisibility,
    };
};