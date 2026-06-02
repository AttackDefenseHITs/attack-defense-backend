import React from "react";
import { Button, Card, List, Skeleton } from "antd";

export default function EventsFeedCard({
                                           themeStyles,
                                           loading,
                                           events = [],
                                           onViewAll,
                                       }) {
    const cardStyle = {
        backgroundColor: themeStyles.cardBackground,
        border: `1px solid ${themeStyles.cardBorder}`,
        boxShadow: themeStyles.cardShadow,
        borderRadius: 12,
        overflow: "hidden",
    };

    const ITEM_HEIGHT = 47;
    const VISIBLE_ITEMS = 3;
    const needScroll = (events?.length ?? 0) > VISIBLE_ITEMS;
    const maxBodyHeight = ITEM_HEIGHT * VISIBLE_ITEMS;

    return (
        <Card
            title={<span style={{ color: themeStyles.commonText }}>Лента событий</span>}
            headStyle={{
                borderBottom: `1px solid ${themeStyles.cardBorder}`,
            }}
            extra={
                <Button type="link" onClick={onViewAll} style={{ color: themeStyles.text }}>
                    Посмотреть все
                </Button>
            }
            style={cardStyle}
            bodyStyle={{
                padding: 0,
                ...(needScroll ? { maxHeight: maxBodyHeight, overflowY: "auto" } : { overflowY: "hidden" }),
            }}
        >
            {loading ? (
                <div style={{ padding: 16 }}>
                    <Skeleton active />
                </div>
            ) : (
                <List
                    dataSource={events}
                    locale={{
                        emptyText: (
                            <span style={{ color: themeStyles.secondaryText }}>
                                Пока нет событий
                            </span>
                        ),
                    }}
                    renderItem={(item) => (
                        <List.Item style={{ padding: "12px 24px" }}>
                            <div style={{ width: "100%" }}>
                                <div style={{ display: "flex", justifyContent: "space-between", gap: 8 }}>
                                    <span style={{ color: themeStyles.commonText, fontWeight: 500 }}>
                                      {item.text}
                                    </span>
                                    <span style={{ color: themeStyles.secondaryText, whiteSpace: "nowrap" }}>
                                        {item.time}
                                    </span>
                                </div>
                            </div>
                        </List.Item>
                    )}
                />
            )}
        </Card>
    );
}
