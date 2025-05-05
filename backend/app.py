from flask import jsonify

@app.route('/api/admin/initialize', methods=['POST'])
def initialize_data():
    try:
        # 这里调用全量更新的逻辑
        # 例如：update_all_stock_data()
        return jsonify({
            'success': True,
            'message': '全量更新已开始'
        })
    except Exception as e:
        return jsonify({
            'success': False,
            'message': str(e)
        }), 500

@app.route('/api/admin/incremental-update', methods=['POST'])
def incremental_update():
    try:
        # 这里调用增量更新的逻辑
        # 例如：update_recent_stock_data()
        return jsonify({
            'success': True,
            'message': '增量更新已开始'
        })
    except Exception as e:
        return jsonify({
            'success': False,
            'message': str(e)
        }), 500 