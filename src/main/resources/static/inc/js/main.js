import * as THREE from 'three';
import WebGL from 'three/addons/capabilities/WebGL.js';
import { FontLoader } from 'three/addons/loaders/FontLoader.js';
import { TextGeometry } from 'three/addons/geometries/TextGeometry.js';
import { ARButton } from 'three/addons/webxr/ARButton.js';

let camera, textMesh;

//Renderer
const renderer = new THREE.WebGLRenderer();

//Scene
const scene = new THREE.Scene();
scene.background = new THREE.Color( 0x000000 );

//Group
const group = new THREE.Group(); 

let counterValue = '0';
const materials = new THREE.MeshPhongMaterial( { color: 0xffffff } );

const loader = new FontLoader();
let font;
let geometry;

//Initialize everything that has to be rendered
init();

// Start event stream with the server
startEventStream();

//Start rendering
startRendering();

function init() {
  
  setupRenderer();

  setupLights();
  
  setupGroup();
  
  setupFont();

  const cameraPosition = [0, 0, 50];

  setupCamera(75, window.innerWidth / window.innerHeight, 0.1, 100, cameraPosition);

  window.addEventListener( 'resize', onWindowResize );
}

//Set the renderer size and append it to the html page (it is the canvas)
function setupRenderer() {
  renderer.setSize( window.innerWidth, window.innerHeight, false );
  document.body.appendChild( renderer.domElement );
  
  //Create the AR button so that the user can start a AR session
  document.body.appendChild( ARButton.createButton( renderer ) );

  renderer.xr.enabled = true;
}

//Initialize the camera based and the params: 'PoV', 'ratio', 'near', 'far' (objects will be rendered if they are far between 'near' and 'far' values from the camera)
//X, y, z is the starting position of the camera
function setupCamera(pov, ratio, near, far, [x, y, z]) {
  camera = new THREE.PerspectiveCamera( pov, ratio, near, far );
  camera.position.set( x, y, z );
}

//Initialize the light in order to see the Text
function setupLights() {
  const dirLight = new THREE.DirectionalLight( 0xffffff, 0.125 );
  dirLight.position.set( 0, 0, 1 ).normalize();
  scene.add( dirLight );

  const pointLight = new THREE.PointLight( 0xffffff, 1.5 );
  pointLight.color.setHSL( Math.random(), 1, 0.5 );
  pointLight.position.set( 0, 0, 90 );
  scene.add( pointLight );
}

function setupGroup() {
  group.position.set(-3, 0, 0);

  scene.add( group );
}

function setupFont() {
  loader.load( '/inc/fonts/Helvetica.json', function ( f ) {
    font = f
  } );
}

function onWindowResize() {
  const canvas = renderer.domElement;
  camera.aspect = canvas.clientWidth / canvas.clientHeight;
  camera.updateProjectionMatrix();

  renderer.setSize( canvas.clientWidth, canvas.clientHeight, false );

}

//See it WebGL is available then start rendering
function startRendering() {
  if (WebGL.isWebGLAvailable()) {
    animate();
  } else {
    const warning = WebGL.getWebGLErrorMessage();
    document.getElementById( 'warnings' ).appendChild( warning );
  }
}

//Rendering
function animate() {
	renderer.setAnimationLoop(() => {
    renderer.clear();
    renderer.render( scene, camera ); 
  }); 
}

//Refresh the text whenever the text changes
function refreshText() {
  group.remove(textMesh);
  if (geometry) {
    geometry.dispose();
  }

  createAndAddText();
}

//Create and add to the group the text to be displayed
function createAndAddText() {
    geometry = new TextGeometry( counterValue, {
      font: font,
      size: 20,
      height: 5,
      curveSegments: 14,
      bevelEnabled: false
    } );
    
    geometry.computeBoundingBox();

    const centerOffset = - 0.5 * ( geometry.boundingBox.max.x - geometry.boundingBox.min.x );

    textMesh = new THREE.Mesh( geometry, materials );

    textMesh.position.set(centerOffset, 0, 0);

    group.add(textMesh);  
}

// UPDATE COUNTER CODE
  
// Start the event stream
function startEventStream() {
  const eventSource = new EventSource('/event-stream');

  //Update the value of the counter then refresh the text
  eventSource.onmessage = function (event) {
    counterValue = event.data.toString();
    refreshText();
  };

  eventSource.onerror = function () {
    console.error('Error occurred in event stream');
    eventSource.close();
    setTimeout(startEventStream, 5000);
  };
}

