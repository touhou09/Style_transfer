from fastapi import FastAPI
from routers import items, example_images
# from .dependencies import some_common_dependency

app = FastAPI()

app.include_router(items.router, prefix="/items")
app.include_router(example_images.router)