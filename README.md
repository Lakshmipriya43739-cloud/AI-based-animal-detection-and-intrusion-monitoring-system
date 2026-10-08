# 🐾 AI-Based Animal Detection and Intrusion Monitoring System

An AI-powered animal detection and farm intrusion monitoring system designed to detect animals entering restricted agricultural areas and provide real-time alerts to farmers and authorized officers.

The system uses computer vision and deep learning to identify animals from images/video feeds and monitor intrusion zones. When an animal is detected inside a defined area, the system can generate an alert notification to help farmers respond quickly and protect their crops.

---

## 🚀 Features

- 🐾 AI-based animal detection
- 📹 Image/video-based monitoring
- 🎯 Intrusion zone monitoring
- 🚨 Real-time intrusion alerts
- 📱 Farmer notification support
- 👮 Officer/admin monitoring
- 🔐 User authentication
- 📊 Monitoring dashboard
- 🌐 Web-based frontend
- ⚡ Fast AI inference using ONNX Runtime
- 🖼️ OpenCV-based image preprocessing

---

## 🧠 AI Detection

The system uses a **YOLO11n ONNX model** for object detection.

### Detection Pipeline

```text
Camera / Image
      ↓
Image Preprocessing
      ↓
YOLO11n ONNX Model
      ↓
Object Detection
      ↓
Intrusion Zone Check
      ↓
Animal Detected?
      ↓
Alert Generation
      ↓
Farmer / Officer Notification
