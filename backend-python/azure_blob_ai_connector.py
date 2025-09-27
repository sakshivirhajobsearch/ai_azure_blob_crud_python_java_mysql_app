import os
from dotenv import load_dotenv
from azure.storage.blob import BlobServiceClient, ContainerClient

# Load environment variables from .env
load_dotenv()

# Fetch environment variables
connection_str = os.getenv("AZURE_STORAGE_CONNECTION_STRING")
container_name = os.getenv("AZURE_BLOB_CONTAINER")
sas_url = os.getenv("AZURE_BLOB_SAS_URL")  # Prefer container-specific SAS URL

# Validation
if not container_name:
    raise ValueError("❌ Missing AZURE_BLOB_CONTAINER in .env")

# Initialize ContainerClient
try:
    if sas_url:
        # SAS URL must include container name: https://<account>.blob.core.windows.net/<container>?<SAS>
        container_client = ContainerClient.from_container_url(sas_url)
    elif connection_str:
        blob_service_client = BlobServiceClient.from_connection_string(connection_str)
        container_client = blob_service_client.get_container_client(container_name)
    else:
        raise ValueError("❌ Must provide either AZURE_STORAGE_CONNECTION_STRING or AZURE_BLOB_SAS_URL")
except Exception as e:
    raise RuntimeError("❌ Failed to create container client: " + str(e))

# List blobs
try:
    print(f"📄 Listing blobs in container: {container_name}")
    for blob in container_client.list_blobs():
        print("📦", blob.name)
except Exception as e:
    raise RuntimeError("❌ Failed to list blobs: " + str(e))
