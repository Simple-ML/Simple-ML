
document.addEventListener('mousemove', logMouseObject);

const mouseDataWrapper = {};

function logMouseObject(e) {
    mouseDataWrapper.data = e;
}

export default mouseDataWrapper;