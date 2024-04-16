from fastapi import FastAPI
from routers import items, generate_images
# from .dependencies import some_common_dependency

app = FastAPI()

app.include_router(items.router, prefix="/items")

app.include_router(generate_images.router)