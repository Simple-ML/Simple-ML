import React, { Component } from 'react';

class ContextMenu extends Component {
  constructor() {
    super();
    
    this.state = {
      showMenu: false,
    }
  }

  render() {
    return (
      <div>
        <button>
          Show menu
        </button>
        
        {
          this.state.showMenu
            ? (
              <div className="menu">
                <button> Menu item 1 </button>
                <button> Menu item 2 </button>
                <button> Menu item 3 </button>
              </div>
            )
            : (
              null
            )
        }
      </div>
    );
  }
}
export default ContextMenu