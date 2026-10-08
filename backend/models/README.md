# YOLO ONNX Model Placement

Place your exported YOLOv8 ONNX model file here.

## Default expected filename
```
backend/models/yolov8n.onnx
```
This is controlled by the `ai.model.path` property in `application.yml`.

## How to export YOLOv8 to ONNX

```python
# Using the ultralytics Python package (export only — inference runs in Java)
from ultralytics import YOLO

model = YOLO("yolov8n.pt")   # or yolov8s.pt, yolov8m.pt, etc.
model.export(format="onnx", imgsz=640, opset=12)
# Produces: yolov8n.onnx
```

Copy the resulting `.onnx` file to this directory.

## Class mapping

Update `ai.class-names` in `application.yml` to match the class indices
used in your specific trained model:

```yaml
ai:
  class-names:
    0: Elephant
    1: Tiger
    2: Leopard
    3: Deer
    4: Wild Boar
    5: Bear
    6: Monkey
```

## Stub mode

If the model file is not present, the application starts normally in **stub mode**:
- All other APIs (auth, animals, zones, recommendations) work as expected.
- `/api/ai/detect` returns an empty detections array with a note about stub mode.
- No errors are thrown.
