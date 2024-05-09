from fastapi import FastAPI
<<<<<<< HEAD
from routers import items, generate_images
=======
from routers import items, example_images
>>>>>>> exampleImage
# from .dependencies import some_common_dependency

app = FastAPI()

app.include_router(items.router, prefix="/items")
<<<<<<< HEAD

app.include_router(generate_images.router)
=======
app.include_router(example_images.router)
>>>>>>> exampleImage
