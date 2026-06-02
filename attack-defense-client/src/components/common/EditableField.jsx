import React from "react";
import { Input, Button, Typography, Space, Tooltip } from "antd";
import { EditOutlined, CheckOutlined, CloseOutlined } from "@ant-design/icons";
import { motion } from "framer-motion";

const { Text } = Typography;

const EditableField = ({
  label,
  value,
  field,
  isEditing,
  onEdit,
  onSave,
  onCancel,
  onChange,
  textColor = "#000",
  editIconColor = "#1890ff",
  saveIconColor = "green",
  cancelIconColor = "red",
  backgroundColor = "#fff"
}) => (
  <motion.div 
    initial={{ opacity: 0.9 }}
    animate={{ opacity: 1 }}
    transition={{ duration: 0.2 }}
    style={{ 
      display: "flex", 
      justifyContent: "space-between", 
      alignItems: "center",
      borderRadius: "6px",
      backgroundColor: isEditing ? `${backgroundColor}22` : "transparent",
      border: isEditing ? `1px solid ${editIconColor}33` : "1px solid transparent",
      transition: "all 0.2s ease"
    }}
  >
    <Space direction="vertical" size={isEditing ? 4 : 0} style={{ flex: 1 }}>
      <Text strong style={{ fontSize: "13px", color: textColor, opacity: 0.8 }}>
        {label}
      </Text>
      
      {isEditing ? (
        <motion.div
          initial={{ y: -5, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 0.2 }}
          style={{ width: "100%" }}
        >
          <Input
            value={value}
            onChange={(e) => onChange(e.target.value)}
            autoFocus
            style={{ 
              width: "100%", 
              background: backgroundColor, 
              color: textColor,
              borderRadius: "4px",
              height: "32px",
              borderColor: editIconColor
            }}
          />
        </motion.div>
      ) : (
        <Text style={{ fontSize: "15px", color: textColor }}>
          {value || <span style={{ opacity: 0.5, fontStyle: "italic" }}>Не указано</span>}
        </Text>
      )}
    </Space>
    
    <div>
      {isEditing ? (
        <Space size={4}>
          <Tooltip title="Сохранить">
            <Button
              type="text"
              size="small"
              icon={<CheckOutlined style={{ color: saveIconColor }} />}
              onClick={() => onSave(field)}
              style={{ 
                padding: 0, 
                width: "28px", 
                height: "28px", 
                display: "flex", 
                alignItems: "center", 
                justifyContent: "center",
                borderRadius: "4px",
                background: `${saveIconColor}11`
              }}
            />
          </Tooltip>
          <Tooltip title="Отменить">
            <Button
              type="text"
              size="small"
              icon={<CloseOutlined style={{ color: cancelIconColor }} />}
              onClick={onCancel}
              style={{ 
                padding: 0, 
                width: "28px", 
                height: "28px", 
                display: "flex", 
                alignItems: "center", 
                justifyContent: "center",
                borderRadius: "4px",
                background: `${cancelIconColor}11`
              }}
            />
          </Tooltip>
        </Space>
      ) : (
        <Tooltip title="Редактировать">
          <Button
            type="text"
            size="small"
            icon={<EditOutlined style={{ color: editIconColor }} />}
            onClick={() => onEdit(field)}
            style={{ 
              padding: 0, 
              width: "28px", 
              height: "28px", 
              display: "flex", 
              alignItems: "center", 
              justifyContent: "center",
              borderRadius: "4px",
              background: `${editIconColor}11`
            }}
          />
        </Tooltip>
      )}
    </div>
  </motion.div>
);

export default EditableField;
