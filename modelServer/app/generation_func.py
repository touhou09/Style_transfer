import styleAlign.inversion as inversion
import numpy as np
from diffusers.utils import load_image
import styleAlign.sa_handler as sa_handler
import torch

def inverse_reference_image(pipeline, ref_prompt, ref_image_path):
    
    num_inference_steps = 50
    x0 = np.array(load_image(ref_image_path).resize((1024, 1024)))
    zts = inversion.ddim_inversion(pipeline, x0, ref_prompt, num_inference_steps, 2)
    zT, inversion_callback = inversion.make_inversion_callback(zts, offset=5)
    
    return zT, inversion_callback

def generate_image(pipeline, prompt_list, ref_prompt, ref_latent, inversion_callback, device):

    num_inference_steps = 50
    
    prompt_list.insert(0, ref_prompt)
    
#     shared_score_shift = np.log(2)  # higher value induces higher fidelity, set 0 for no shift
    shared_score_shift = 0
#     shared_score_scale = 1.0  # higher value induces higher, set 1 for no rescale
    shared_score_scale = 0.75
    
    handler = sa_handler.Handler(pipeline)
    sa_args = sa_handler.StyleAlignedArgs(
        share_group_norm=True, share_layer_norm=True, share_attention=True,
        adain_queries=True, adain_keys=True, adain_values=False,
        shared_score_shift=shared_score_shift, shared_score_scale=shared_score_scale,)
    handler.register(sa_args)

    g_cpu = torch.Generator(device='cpu')
    g_cpu.manual_seed(10)

    latents = torch.randn(len(prompt_list), 4, 128, 128, device='cpu', generator=g_cpu,
                          dtype=pipeline.unet.dtype,).to(device)
    
    latents[0] = ref_latent

    generated_images = pipeline(prompt_list, latents=latents,
                        callback_on_step_end=inversion_callback,
                        num_inference_steps=num_inference_steps, guidance_scale=10.0).images

    handler.remove()
    
    return generated_images