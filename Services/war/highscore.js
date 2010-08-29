
Highscore = {};

Highscore.init = function() {	
	var store = new Ext.data.JsonStore({
	    url: 'highscores',
	    root: 'highscores',
	    fields: [
	        {name:'id', type: 'int'},
			{name:'name', type: 'string'},
	        {name:'score', type: 'int'},
	        {name:'date', type:'date', dateFormat:'time'}
	    ]
	});
	store.load();
	
	var tpl = new Ext.XTemplate(
		    '<tpl for=".">',
		        '<div class="thumb-wrap" id="{name}">',
		        '<div class="thumb"><img src="highscores/{id}" title="{name}"></div>',
		        '<span class="x-editable">{name}</span></div>',
		    '</tpl>',
		    '<div class="x-clear"></div>'
		);
	
	new Ext.Viewport({
		layout:'vbox',
	    title: 'Highscore listing',
	    defaults: {
	    	border: false
	    },
	    
	    items: [{html: 'Viper Highscores',margins: '20 0 10 50'},{
	    	xtype:'listview',
	    	store: store,
	    	width: 400,
	    	columns: [{
		    	header: 'Name',
		    	width: .3,
		        dataIndex: 'name'
		    },
		    {
		    	header: 'Score',
		    	width: .1,
		        dataIndex: 'score'
		    },
		    {
		    	header: 'Date',
		    	width: .4,
		        dataIndex: 'date',
		        tpl: '{date:date("M j Y H:i")}'
		    },
		    {
		    	header: 'Image',
		    	width: .2,
		        dataIndex: 'id',
		        tpl: '<img src="highscores/{id}.png" width="60" height"60">'
		    }],
		    margins: '0 0 0 50',
	    }]
	    
	});
};

Ext.onReady(Highscore.init);