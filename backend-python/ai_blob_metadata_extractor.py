# ai_blob_metadata_extractor.py

import warnings
warnings.simplefilter(action='ignore', category=FutureWarning)

import os
import torch
from transformers import AutoModelForImageClassification, AutoImageProcessor
from PIL import Image

# -----------------------------
# Load the model and processor
# -----------------------------
MODEL_NAME = "google/vit-base-patch16-224"

# Use AutoImageProcessor instead of deprecated ViTFeatureExtractor
processor = AutoImageProcessor.from_pretrained(MODEL_NAME)
model = AutoModelForImageClassification.from_pretrained(MODEL_NAME)

# Set device (GPU if available)
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)
model.eval()

# -----------------------------
# Function to extract AI metadata
# -----------------------------
def extract_ai_metadata(image_path: str) -> dict:
    """
    Extract predicted label and confidence from an image.
    
    Args:
        image_path (str): Path to the image file.

    Returns:
        dict: {'label': str, 'confidence': float}
    """
    if not image_path or not isinstance(image_path, str):
        raise ValueError("Invalid image path provided.")

    if not os.path.exists(image_path):
        raise FileNotFoundError(f"Image file not found: {image_path}")

    # Load image
    image = Image.open(image_path).convert("RGB")

    # Preprocess image
    inputs = processor(images=image, return_tensors="pt").to(device)

    # Forward pass
    with torch.no_grad():
        outputs = model(**inputs)
        logits = outputs.logits
        probabilities = torch.nn.functional.softmax(logits, dim=-1)
        confidence, predicted_class_idx = torch.max(probabilities, dim=-1)

    # Get human-readable label
    predicted_label = model.config.id2label[predicted_class_idx.item()]

    return {"label": predicted_label, "confidence": confidence.item()}

# -----------------------------
# Optional: local test
# -----------------------------
if __name__ == "__main__":
    test_image_path = os.path.join("test_images", "test_image.jpg")
    if os.path.exists(test_image_path):
        metadata = extract_ai_metadata(test_image_path)
        print(f"Predicted label: {metadata['label']}")
        print(f"Confidence: {metadata['confidence']:.4f}")
    else:
        print(f"Test image not found at {test_image_path}. Please add a sample image to test.")
