from fastapi import FastAPI
from routers import example_images, generate_images

app = FastAPI()
app.include_router(generate_images.router)
app.include_router(example_images.router)
