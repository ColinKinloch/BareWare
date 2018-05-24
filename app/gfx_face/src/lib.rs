#[cfg(target_os = "android")]
extern crate android_glue;
#[cfg(target_os = "android")]
extern crate android_injected_glue;

#[macro_use]
extern crate lazy_static;

extern crate rand;

extern crate egli;
//#[macro_use]
//extern crate gfx;
//extern crate gfx_window_glutin;
extern crate gfx_device_gl;
//extern crate glutin;

#[macro_export]
macro_rules! log {
    ( $( $e:expr ),* ) => ( $crate::android_glue::write_log(&format!($($e),*)) );
}

use std::sync::{Arc, Mutex};
use std::sync::mpsc::{Receiver, channel};
use std::ptr;

use android_glue::write_log;

//use gfx::traits::FactoryExt;
//use gfx::Device;
//use glutin::GlContext;

//pub type ColorFormat = gfx::format::Rgba8;
//pub type DepthFormat = gfx::format::DepthStencil;

fn main() {
  android_glue::write_log("main has been called, yo");
}

struct Renderer {
  // display: Arc<egli::Display>,
  surface: Option<gfx_device_gl::Surface>,
}

impl Renderer {
  fn new() -> Result<Renderer, Box<std::error::Error>> {
    println!("println works, ayayayayayaya");
    write_log("hey");
    let display = egli::Display::from_default_display().unwrap();
    log!("EGL: {:?}", display.initialize_and_get_version());
    log!("EGL_CLIENT_APIS {}", display.query_client_apis().unwrap());
    
    Ok(Renderer {
      // display: display,
      surface: None,
    })
  }
  fn create_surface(&mut self, mut window: *mut android_injected_glue::ffi::ANativeWindow) {
    
    /*let configs = self.display.config_filter()
        .with_red_size(8)
        .with_green_size(8)
        .with_blue_size(8)
        .choose_configs().unwrap();
    let config = configs[0];
    
    let mut format = config.native_visual_id().unwrap();
    
    let w_ptr: *mut android_injected_glue::ffi::ANativeWindow = unsafe { window.get_mut() };
    unsafe { android_injected_glue::ffi::ANativeWindow_setBuffersGeometry(w_ptr, 0, 0, format) };
    
    let surface = self.display.create_window_surface(config, w_ptr).unwrap();
    self.surface = Some(surface);*/
    /*//let mut events_loop = glutin::EventsLoop::new();
    write_log("hey");
    let window_config = glutin::WindowBuilder::new()
      .with_title("Triangle example".to_string())
      .with_dimensions(1024, 768);
    write_log("hey");
    let context = glutin::ContextBuilder::new();
    write_log("hey");
    let (window, mut device, mut factory, main_color, mut main_depth) =
      gfx_window_glutin::init::<ColorFormat, DepthFormat>(window_config, context);
    write_log("hey");
    */
    //  .with_gl(glutin::GlRequest::Specific(api, version))
    //  .with_vsync(true);
  }
  fn set_surface_for_anativewindow(self, window: *mut android_injected_glue::ffi::ANativeWindow) {
    //self.surface = Some(gfx_device_gl::(window));
  }
}

#[cfg(target_os = "android")]
pub mod wallpaper {
  use std;
  use rand;
  use std::sync::{Arc, Mutex};
  use android_injected_glue::ffi;
  use android_injected_glue::write_log;
  use std::collections::HashMap;
  use self::ffi::{JNIEnv, jobject};
  
  type Id = i16;
  
  use super::{Renderer};
  
  lazy_static! {
    // TODO: Is there a better way
    static ref WALLPAPERS: Mutex<HashMap<Id, Arc<Mutex<Renderer>>>> = Mutex::new(HashMap::new());
  }
  
  fn connect() -> Id {
    let id = rand::random();
    write_log(format!("connecting: {}", id).as_str());
    assert!(WALLPAPERS.lock().unwrap().insert(id, Arc::new(Mutex::new(Renderer::new().unwrap()))).is_none());
    write_log(format!("connected: {}", id).as_str());
    id
  }
  fn disconnect(id: Id) {
    write_log(format!("disconnecting: {}", id).as_str());
    let wp = WALLPAPERS.lock().unwrap().remove(&id).unwrap();
    /*let thread = {
      let mut instance = instance.lock().unwrap();
      instance.phase = Phase::Stopping;
      instance.thread.take()
    };
    if let Some(thread) = thread {
      thread.join();
    };*/
    write_log("disconnected");
  }
  fn set_surface(id: Id, window: *mut ffi::ANativeWindow) {
    let wps = WALLPAPERS.lock().unwrap();
    if let Some(wp) = wps.get(&id) {
      if let Ok(wp) = wp.lock() {
        // wp.create_surface(window);
        // create_surface_from_anativewindow(instance, window)
      }
    }
  }
  
  #[no_mangle]
  #[inline(never)]
  #[allow(non_snake_case)]
  pub extern "C" fn Java_org_kinloch_colin_wear_bare_gfx_GfxFace_connect(_: *mut JNIEnv, _: jobject) -> Id {
    connect()
  }
  #[no_mangle]
  #[inline(never)]
  #[allow(non_snake_case)]
  pub extern "C" fn Java_org_kinloch_colin_wear_bare_gfx_GfxFace_disconnect(_: *mut JNIEnv, _: jobject, id: Id) {
    disconnect(id)
  }
  #[no_mangle]
  #[inline(never)]
  #[allow(non_snake_case)]
  pub extern "C" fn Java_org_kinloch_colin_wear_bare_gfx_GfxFace_setSurface(env: *mut JNIEnv, _: jobject, id: Id, surface: jobject) {
    let window = unsafe { ffi::ANativeWindow_fromSurface(env, surface) };
    set_surface(id, window)
  }
}

#[cfg(target_os = "android")]
#[no_mangle]
#[inline(never)]
#[allow(non_snake_case)]
pub extern "C" fn android_main(app: *mut ()) {
    use std::env;
    env::set_var("RUST_BACKTRACK", "1");
    android_injected_glue::android_main2(app as *mut _, move |_, _| main());
}
